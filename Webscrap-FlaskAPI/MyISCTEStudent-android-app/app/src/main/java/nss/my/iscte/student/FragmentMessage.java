package nss.my.iscte.student;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FragmentMessage extends Fragment implements MessageAdapter.MessageAdapterListener {
	private static final String TAG = FragmentMessage.class.getSimpleName();
	private List<Message> messageList = new ArrayList<>();
	private RecyclerView recyclerView;
	private MessageAdapter mAdapter;
	private SearchView searchView;
	private SearchView.OnQueryTextListener queryTextListener;
	protected MyISCTEDatasource ds;
	ProgressDialog progressDialog;

	SwipeRefreshLayout swipeRefreshLayout ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_message, container, false);

		recyclerView =  (RecyclerView) view.findViewById(R.id.msgList);
		swipeRefreshLayout =  (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

		messageList = new ArrayList<>();
		mAdapter = new MessageAdapter(getContext(), messageList, this);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
		recyclerView.setAdapter(mAdapter);

		progressDialog = new ProgressDialog(getContext());

		prepareMessageData();


		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				prepareMessageData();
				swipeRefreshLayout.setRefreshing(false);
			}
		});

		return view;
	}

	// Method to show Progress bar
	private void showProgressDialogWithTitle(String substring) {
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//Without this user can hide loader by tapping outside screen
		progressDialog.setCancelable(false);
		progressDialog.setMessage(substring);
		progressDialog.show();
	}

	// Method to hide/ dismiss Progress bar
	private void hideProgressDialogWithTitle() {
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.dismiss();
	}

	public static FragmentMessage newInstance() {
		return new FragmentMessage();
	}


	private void prepareMessageData() {
		ds = new MyISCTEDatasource(getContext());
		ds.open();

		if (ds.countMessage() > 0) {
			for (int i = 0; i < ds.getAllMessages().size(); i++) {
				messageList.add(ds.getAllMessages().get(i));
			}

			hideProgressDialogWithTitle();
		}else{
			showProgressDialogWithTitle("Carregando mensagens do Fenix ...");
		}
		mAdapter.notifyDataSetChanged();
		hideProgressDialogWithTitle();

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
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.menu, menu);
		MenuItem searchItem = menu.findItem(R.id.menuSearch);
		SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

		if (searchItem != null) {
			searchView = (SearchView) searchItem.getActionView();
		}
		if (searchView != null) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
			searchView.setQueryHint("Search Message...");
			queryTextListener = new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextChange(String query) {
					Log.i("onQueryTextChange", query);
					mAdapter.getFilter().filter(query);
					return true;
				}
				@Override
				public boolean onQueryTextSubmit(String query) {
					Log.i("onQueryTextSubmit", query);
					mAdapter.getFilter().filter(query);
					return true;
				}
			};
			searchView.setOnQueryTextListener(queryTextListener);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onMessageSelected(Message message) {
		Toast.makeText(getContext().getApplicationContext(), "Selected: " + message.getsSubject() + ", " + message.getsFrom(), Toast.LENGTH_LONG).show();
	}

}
