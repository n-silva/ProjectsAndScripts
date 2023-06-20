package nss.my.iscte.student;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;


public final class Utils {
	protected String key = "123456789qwerty$";
	protected Utils() {}
	protected static Context context;
	public static void msg(Context context, String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static String getLocalIpAddress() {

		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration <InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String res = inetAddress.getHostAddress().toString();
						Log.d("VM Android IP: ", res);
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {

			Log.e("VM Android IP: ", ex.toString());
		}
		return null;
	}

	public static ArrayList<String> nameOfEvent = new ArrayList<String>();
	public static ArrayList<String> startDates = new ArrayList<String>();
	public static ArrayList<String> endDates = new ArrayList<String>();
	public static ArrayList<String> descriptions = new ArrayList<String>();

	public static ArrayList<String> readCalendarEvent(Context context) {
		Uri content = Uri.parse("content://com.android.calendar/events");
		String[] vec = new String[] { "calendar_id", "title", "description", "dtstart", "dtend", "allDay", "eventLocation" };
		String selectionClause = "(dtstart >= ? AND dtend <= ?) OR (dtstart >= ? AND allDay = ?)";
		String dtstart = "2019/06/01";
		String dtend = "2019/06/31";
		String[] selectionsArgs = new String[]{"" + dtstart, "" + dtend, "" + dtstart, "1"};
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(content, vec, selectionClause, selectionsArgs, null);

		cursor.moveToFirst();
		// fetching calendars name
		String CNames[] = new String[cursor.getCount()];

		// fetching calendars id
		nameOfEvent.clear();
		startDates.clear();
		endDates.clear();
		descriptions.clear();

		for (int i = 0; i < CNames.length; i++) {

			Log.d("CALENDAREVENT",cursor.getString(0));
			Log.d("CALENDAREVENT",cursor.getString(1));
			Log.d("CALENDAREVENT",cursor.getString(2));
			Log.d("CALENDAREVENT",cursor.getString(3));
			Log.d("CALENDAREVENT",cursor.getString(4));
			Log.d("CALENDAREVENT",cursor.getString(5));
			Log.d("CALENDAREVENT",cursor.getString(6));

			nameOfEvent.add(cursor.getString(1));
			startDates.add(getDate(Long.parseLong(cursor.getString(3))));
			endDates.add(getDate(Long.parseLong(cursor.getString(4))));
			descriptions.add(cursor.getString(2));
			CNames[i] = cursor.getString(1);
			cursor.moveToNext();

		}
		return nameOfEvent;
	}

	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	private Date convertStringToDate(String dateInString){
		SimpleDateFormat format = new SimpleDateFormat("d-MM-yyyy", Locale.ENGLISH);
		Date date = null;
		try {
			date = format.parse(dateInString);
		} catch (ParseException | java.text.ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
}
