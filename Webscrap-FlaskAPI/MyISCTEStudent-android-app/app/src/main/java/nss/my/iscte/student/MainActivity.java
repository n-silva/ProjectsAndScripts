package nss.my.iscte.student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	protected  String key = new Utils().key;
	private String mail, password;
	static public MyISCTEDatasource ds;
	Fragment selectedFragment = null;
	private List<Message> messageList = new ArrayList<>();
	private List<MyEvents> eventList = new ArrayList<>();
	private RequestQueue mRequestQueue;
	private MessageTask mAuthTask = null;
	private static final String TAG = MainActivity.class.getName();
	protected  SharedPreferences settings;
	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {

			switch (item.getItemId()) {
				case R.id.navigation_home:
					getSupportActionBar().setTitle("Home");
					selectedFragment = FragmentHome.newInstance();
					if (settings.getBoolean("updateProfile", false)){
						msg("Update profile needed.");
					}
					break;
				case R.id.navigation_message:
					getSupportActionBar().setTitle("Message");
					selectedFragment = FragmentMessage.newInstance();
					//mAdapter = ((FragmentMessage) selectedFragment).getAdpter();
					/**
					if (settings.getBoolean("updateMessage", false)){
						try {
							getMessage();
						} catch (Exception e) {
							e.printStackTrace();
							msg("Erro ao tentar carregar dados da rede.");
						}
					}
					 */
					break;
				case R.id.navigation_people:
					getSupportActionBar().setTitle("People");
					selectedFragment = FragmentContact.newInstance();
					//mTextMessage.setText(R.string.title_search);
					break;
				case R.id.navigation_calendar:
					getSupportActionBar().setTitle("Calendar");
					selectedFragment = FragmentCalendar.newInstance();
					//mTextMessage.setText(R.string.title_calendar);
					/**
					if (settings.getBoolean("updateCalendar", false)){
						getEvents();
						SharedPreferences.Editor editor = settings.edit();
						settings.edit().putBoolean("updateMessage",false);
						settings.edit().commit();
						getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
					}
					*/
					break;
			}
			openFragment(selectedFragment);
			return true;
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

		ds = new MyISCTEDatasource(this);
		ds.open();

		BottomNavigationView navView = findViewById(R.id.nav_view);
		navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		mail = settings.getString("email", "");
		password = settings.getString("password", "");
		getEvents();
		myToolbar.setTitle("My ISCTE - Student " + mail);
		setSupportActionBar(myToolbar);

		if (findViewById(R.id.frContainer) != null) {
			if (savedInstanceState != null) {
				return;
			}
			getSupportActionBar().setTitle("Home");
			FragmentHome selectedFragment = FragmentHome.newInstance();
			openFragment(selectedFragment);
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menuAbout:
				Toast.makeText(this, "You clicked about", Toast.LENGTH_SHORT).show();
				break;
			case R.id.menuLogout:
				Toast.makeText(this, "Sai da aplicação", Toast.LENGTH_SHORT).show();
				SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedpreferences.edit();
				editor.clear();
				editor.commit();

				Intent Login_intent = new Intent(getApplicationContext(),LoginActivity.class);
				startActivity(Login_intent);
				finish();
				break;
		}
		return true;
	}

	private void openFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.frContainer, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void getMessage() throws Exception{
		if (mAuthTask != null) {
			return;
		}

		HashMap data = new HashMap();
		data.put("username", mail);
		data.put("password", password);
		String url = getResources().getString(R.string.url) + "message";
		final String finalPassword = password;
		VolleyCallback mresult = new VolleyCallback() {
			@Override
			public void onSuccess(JSONObject mResponse) {
				try {
					JSONObject parentObject = new JSONObject(mResponse.toString());
					if (!mResponse.getString("status").equals("OK")) {
					} else {
						JSONArray responselogin = parentObject.optJSONArray("message");
						ds.apagarMessage(-1);
						for (int i = 0; i < responselogin.length(); i++) {
							JSONObject jresponse = responselogin.getJSONObject(i);
							Message message = new Message(i, jresponse.getString("sFrom"), jresponse.getString("sSubject"), jresponse.getString("sDate"), jresponse.getString("sMsg"));
							messageList.add(message);
							ds.createMessage(jresponse.getString("sFrom"), jresponse.getString("sSubject"), jresponse.getString("sDate"), jresponse.getString("sMsg"));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(VolleyError mError) {
				Log.d("logERROR", mError.toString());
			}
		};

		if (checkInternetConenction()) {
			postData(url, data, mresult);
		}


		mAuthTask = new MessageTask();
		mAuthTask.execute((Void) null);

	}

	public void getEvents(){
		HashMap data = new HashMap();
		data.put("username", mail);
		data.put("password", password);
		String url = getResources().getString(R.string.url) + "calendar";
		final String finalPassword = password;
		VolleyCallback mresult = new VolleyCallback() {
			@Override
			public void onSuccess(JSONObject mResponse) {
				boolean successLogin = true;
				try {
					JSONObject parentObject = new JSONObject(mResponse.toString());
					System.out.println(mResponse.getString("status"));
					if (!mResponse.getString("status").equals("OK")) {
						successLogin = false;
					} else {
						JSONArray responselogin = parentObject.optJSONArray("calendar");
						ds.delEvent(-1);
						for (int i = 0; i < responselogin.length(); i++) {
							JSONObject jresponse = responselogin.getJSONObject(i);
							MyEvents event = new MyEvents(i,jresponse.getString("data"), jresponse.getString("hour"), jresponse.getString("evento"));
							eventList.add(event);
							ds.createEvent(jresponse.getString("data"), jresponse.getString("hour"), jresponse.getString("evento"));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(VolleyError mError) {
				Log.d("logERROR", mError.toString());
			}
		};

		if (checkInternetConenction()) {
			postData(url, data, mresult);
		}
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
		jsonobj.setRetryPolicy(new DefaultRetryPolicy(20000,
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


	public class MessageTask extends AsyncTask<Void, Void, Boolean> {
		MessageTask() {

		}

		@Override
		protected Boolean doInBackground(Void... params) {
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
			if (success) {
				settings.edit().putBoolean("updateMessage",false);
				settings.edit().commit();
				//do something
				msg("Terminado");
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
		}
	}

	public interface VolleyCallback{
		void onSuccess(JSONObject mResponse);
		void onError(VolleyError mError);
	}
}
