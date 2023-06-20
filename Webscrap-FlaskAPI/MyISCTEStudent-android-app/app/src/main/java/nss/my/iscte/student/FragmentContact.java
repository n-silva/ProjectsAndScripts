package nss.my.iscte.student;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

public class FragmentContact extends Fragment implements ContactAdapter.ContactAdapterListener{

	private static final String TAG = FragmentMessage.class.getSimpleName();
	private List<Contact> contactList = new ArrayList<>();
	private RecyclerView recyclerView;
	private View emptyView;
	private ContactAdapter mAdapter;
	private SearchView searchView;
	private SearchView.OnQueryTextListener queryTextListener;
	protected MyISCTEDatasource ds;
	private  String mail, password;
	private ProgressBar spinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.menu, menu);
		MenuItem searchItem = menu.findItem(R.id.menuSearch);
		searchItem.setVisible(true);
		SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		if (searchItem != null) {
			searchView = (SearchView) searchItem.getActionView();
		}
		if (searchView != null) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
			searchView.setQueryHint("Search Contact...");
			queryTextListener = new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextChange(String query) {
					Log.i("onQueryTextChange", query);
					//mAdapter.getFilter().filter(query);
					return true;
				}
				@Override
				public boolean onQueryTextSubmit(String query) {
					Log.i("onQueryTextSubmit", query);
					prepareContactData(query);
					//mAdapter.getFilter().filter(query); //TODO:filter result
					mAdapter.notifyDataSetChanged();
					return true;
				}
			};
			searchView.setOnQueryTextListener(queryTextListener);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contact, container, false);

		SharedPreferences settings =  getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		mail = settings.getString("email", "");
		password = settings.getString("password", "");
		spinner = (ProgressBar) view.findViewById(R.id.progressBar1);
		spinner.setVisibility(View.GONE);
		emptyView = inflater.inflate(R.layout.recycler_view_empty, container, false);
		recyclerView = (RecyclerView) view.findViewById(R.id.personList);
		contactList = new ArrayList<>();
		mAdapter = new ContactAdapter(getContext(), contactList, this);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
		recyclerView.setAdapter(mAdapter);

		//==================
		if (contactList.size() == 0){
			recyclerView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}else{
			emptyView.setVisibility(View.GONE);
			recyclerView.setVisibility(View.VISIBLE);
		}

		return view;
	}

	public static FragmentContact newInstance() {
		return new FragmentContact();
	}

	private void prepareContactData(String nameSearch) {
		ds = new MyISCTEDatasource(getContext());
		ds.open();
		spinner.setVisibility(View.VISIBLE);
		HashMap data = new HashMap();
		data.put("username", mail);
		data.put("password", password);
		data.put("search",nameSearch);

		String url = getResources().getString(R.string.url) + "person";
		final String finalPassword = password;
		MainActivity.VolleyCallback mresult = new MainActivity.VolleyCallback() {
			@Override
			public void onSuccess(JSONObject mResponse) {
				try {
					JSONObject parentObject = new JSONObject(mResponse.toString());
					System.out.println(mResponse.getString("status"));
					if (!mResponse.getString("status").equals("OK")) {
					} else {
						JSONArray responselogin = parentObject.optJSONArray("person");
						contactList.clear();
						for (int i = 0; i < responselogin.length(); i++) {
							JSONObject jresponse = responselogin.getJSONObject(i);
							Contact contact = new Contact(i, jresponse.getString("name"), jresponse.getString("email"), jresponse.getString("type"), jresponse.getString("avatar"));
							contactList.add(contact);
						}

						Log.i("dentro acesso data:", String.valueOf(contactList.size()));
						mAdapter.notifyDataSetChanged();

						//==================
						if ( contactList.size() == 0){
							emptyView.setVisibility(View.VISIBLE);
							recyclerView.setVisibility(View.GONE);
						}else{
							recyclerView.setVisibility(View.VISIBLE);
							emptyView.setVisibility(View.GONE);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					emptyView.setVisibility(View.VISIBLE);
					recyclerView.setVisibility(View.GONE);
					spinner.setVisibility(View.GONE);
				}
			}

			@Override
			public void onError(VolleyError mError) {
				Log.d("logERROR", mError.toString());
			}
		};

		searchContact(url, data, mresult);
	}

	public void searchContact(String url,HashMap data,final MainActivity.VolleyCallback mResultCallback){
		RequestQueue requstQueue = Volley.newRequestQueue(getContext());
		JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(data),
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if(mResultCallback != null){
							mResultCallback.onSuccess(response);
							spinner.setVisibility(View.GONE);

						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						System.out.println(error.networkResponse);
						if(mResultCallback != null){
							mResultCallback.onError(error);
							spinner.setVisibility(View.GONE);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
			case R.id.menuSearch:
				searchView.setOnQueryTextListener(queryTextListener);
				return super.onOptionsItemSelected(item);
			default:
				break;
		}
		return true;
	}

	@Override
	public void onContactSelected(Contact contact) {
		Toast.makeText(getContext().getApplicationContext(), "Selected: " + contact.getName() + ", " + contact.getType(), Toast.LENGTH_LONG).show();
	}
}
