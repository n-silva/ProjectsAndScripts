package nss.my.iscte.student;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

	String key = new Utils().key;
	private List<Message> messageList = new ArrayList<>();
	private UserLoginTask mAuthTask = null;
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;
	private ProgressDialog pDialog;


	protected MyISCTEDatasource ds;
	SharedPreferences sharedpreferences;
	public static final String PREFS_NAME = "Session";
	private static final String TAG = LoginActivity.class.getName();
	private RequestQueue mRequestQueue;
	private StringRequest mStringRequest;
	boolean cancel = false;
	View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ds = new MyISCTEDatasource(this);
		ds.open();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		msg(Utils.getLocalIpAddress());
		String mail = settings.getString("email", "");
		if (mail != ""){
			Intent iLogged = new Intent(this, MainActivity.class);
			finish();
			startActivity(iLogged);
		}
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
					try {
						attemptLogin();
						return true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		});
		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					attemptLogin();
				} catch (Exception e) {
					e.printStackTrace();
					msg("Erro ao fazer login.");
				}
			}
		});
		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
		pDialog = new ProgressDialog(this);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin() throws Exception {
		checkInternetConenction();
		if (mAuthTask != null) {
			return;
		}
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		// Store values at the time of the login attempt.
		final String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password)){
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		}
		else {
			showProgress(false);
			pDialog.setMessage("Loading...");
			pDialog.show();

			Ciphers c = null;
			c = new Ciphers(key);
			password = c.encrypt(password).toString().trim();
			HashMap data = new HashMap();
			data.put("username",email);
			data.put("password",password);


			String url = getResources().getString(R.string.url) + "syncALL";
			final String finalPassword = password;
			VolleyCallback mresult = new VolleyCallback() {
				@Override
				public void onSuccess(JSONObject mResponse) {
					boolean successLogin = true;
					try {
						JSONObject parentObject = new JSONObject(mResponse.toString());
						System.out.println(mResponse.getString("status").toString());
						if (!mResponse.getString("status").equals("OK")) {
							successLogin = false;
							mEmailView.setError(getString(R.string.error_invalid_email) + " - " + mResponse.getString("status"));
							focusView = mEmailView;
							cancel = true;
						} else {
							//get info Profile data
							pDialog.setMessage("Get profile data ...");
							JSONArray responseinfo = parentObject.optJSONArray("info");
							ds.apagarUser(-1);
							for (int i = 0; i < responseinfo.length(); i++) {
								JSONObject jresponse = responseinfo.getJSONObject(i);
								ds.createUser(email, finalPassword, jresponse.getString("cvEstud"), jresponse.getString("dataMatricula"), jresponse.getString("curso"), jresponse.getString("numEstud"),jresponse.getString("avatar") ,jresponse.getJSONArray("avaliacao").toString());
							}
							//get Message data
							pDialog.setMessage("Get Message data ...");
							JSONArray responsemessage = parentObject.optJSONArray("message");
							ds.apagarMessage(-1);
							for (int i = 0; i < responsemessage.length(); i++) {
								JSONObject jresponse = responsemessage.getJSONObject(i);
								Message message = new Message(i, jresponse.getString("sFrom"), jresponse.getString("sSubject"), jresponse.getString("sDate"), jresponse.getString("sMsg"));
								messageList.add(message);
								ds.createMessage(jresponse.getString("sFrom"), jresponse.getString("sSubject"), jresponse.getString("sDate"), jresponse.getString("sMsg"));
							}
							//get Event data
							pDialog.setMessage("Get Events data ...");
							JSONArray responseEvent = parentObject.optJSONArray("calendar");
							ds.delEvent(-1);
							for (int i = 0; i < responseEvent.length(); i++) {
								JSONObject jresponse = responseEvent.getJSONObject(i);
								MyEvents event = new MyEvents(i,jresponse.getString("data"), jresponse.getString("hour"), jresponse.getString("evento"));
								ds.createEvent(jresponse.getString("data"), jresponse.getString("hour"), jresponse.getString("evento"));
							}
							pDialog.setMessage("Almost Done ...");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					pDialog.dismiss();
					pDialog = null;
					if (successLogin) {
							SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString("email", email.toString());
							editor.putString("password", finalPassword.toString());
							editor.putBoolean("updateProfile",true);
							editor.putBoolean("updateMessage",true);
							editor.putBoolean("updateCalendar",true);
							editor.commit();
							Intent Main_intent = new Intent(getApplicationContext(), MainActivity.class);
							finish();
							startActivity(Main_intent);
					}

				}

				@Override
				public void onError(VolleyError mError) {
					Log.d("logERROR",mError.toString());
					pDialog.dismiss();
					pDialog = null;
				}
			};

			if (checkInternetConenction()) {
				postData(url, data, mresult);
			}
			mAuthTask = new UserLoginTask(email, password);
			mAuthTask.execute((Void) null);

		}
	}

	private boolean isEmailValid(String email) {
		//TODO: Replace this with your own logic
		return email.contains("@");
	}

	private boolean isPasswordValid(String password) {
		return password.length() > 4;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
					show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;

		UserLoginTask(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);
			if (success) {
				System.out.println("login sucess");
			} else {
				mPasswordView.setError(getString(R.string.error_login_attempt));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			pDialog.dismiss();
			pDialog = null;
			showProgress(false);

		}
	}

	public interface VolleyCallback {
		void onSuccess(JSONObject mResponse);
		void onError(VolleyError mError);
	}


	public void postData(String url,HashMap data,final VolleyCallback mResultCallback){
		RequestQueue requstQueue = Volley.newRequestQueue(this);
		JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(data),
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if(mResultCallback != null){
							mResultCallback.onSuccess(response);
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(mResultCallback != null){
							mResultCallback.onError(error);
						}
					}
				}
		){
		};
		jsonobj.setRetryPolicy(new DefaultRetryPolicy(60000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requstQueue.add(jsonobj);
	}

	public void msg(String txt){
		Toast.makeText(this,txt, Toast.LENGTH_LONG).show();
	}

	private boolean checkInternetConenction() {
		// get Connectivity Manager object to check connection
		ConnectivityManager connec =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
		// Check for network connections
		if ( connec.getNetworkInfo(0).getState() ==
				android.net.NetworkInfo.State.CONNECTED ||
				connec.getNetworkInfo(0).getState() ==
						android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() ==
						android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
			msg(" Connected ");
			return true;
		}else if (
				connec.getNetworkInfo(0).getState() ==
						android.net.NetworkInfo.State.DISCONNECTED ||
						connec.getNetworkInfo(1).getState() ==
								android.net.NetworkInfo.State.DISCONNECTED  ) {
			msg(" Not Connected ");
			return false;
		}
		return false;
	}
}

