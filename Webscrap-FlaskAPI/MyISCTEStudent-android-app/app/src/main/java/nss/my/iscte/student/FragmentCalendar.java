package nss.my.iscte.student;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FragmentCalendar extends Fragment  implements EventAdapter.EventAdapterListener{
	private static final String TAG = "CalendarActivity";
	private DatePickerDialog.OnDateSetListener mDateSetListener;

	protected MyISCTEDatasource ds;
	private String mail, password;

	private TextView data, hora, evento;
	private List<MyEvents> eventList = new ArrayList<>();
	private RecyclerView recyclerView;
	private EventAdapter mAdapter;
	SwipeRefreshLayout swipeRefreshLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.menu, menu);
		MenuItem searchItem = menu.findItem(R.id.menuSearch);
		searchItem.setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_calendar, container, false);
		SharedPreferences settings = getContext().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
		mail = settings.getString("email", "");
		password = settings.getString("password", "");

		swipeRefreshLayout =  (SwipeRefreshLayout) view.findViewById(R.id.home_swipe_refresh_layout);
		recyclerView =  (RecyclerView) view.findViewById(R.id.event_list);
		eventList = new ArrayList<>();
		mAdapter = new EventAdapter(getContext(), eventList, this);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
		recyclerView.setAdapter(mAdapter);

		final CalendarView mCalendar = (CalendarView) view.findViewById(R.id.mCalendar);
		mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
				String finalDate=year+"/"+month+"/"+dayOfMonth;
				Long startTime = (Long) mCalendar.getDate();
				if (checkLocationPermission()) {
					//TODO: create event calendar
				}
			}
		});

		getEvents();

		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getEvents();
				swipeRefreshLayout.setRefreshing(false);
			}
		});

		return view;
	}

	public  void getEvents(){
		ds = new MyISCTEDatasource(getContext());
		ds.open();
		if (ds.countEvents() > 0) {
			eventList.clear();
			for (int i = 0; i < ds.getAllEvents().size(); i++) {
				eventList.add(ds.getAllEvents().get(i));
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	public boolean checkLocationPermission()
	{
		String permission = "android.permission.WRITE_CALENDAR";
		int res = getContext().checkCallingOrSelfPermission(permission);
		return (res == PackageManager.PERMISSION_GRANTED);
	}

	public static FragmentCalendar newInstance() {
		return new FragmentCalendar();
	}

	@Override
	public void onEventSelected(MyEvents event) {

	}

}
