package nss.my.iscte.student;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context c) {
		super(c, "MY_ISCTE_DATABASE", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE user (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"email VARCHAR(128)," +
				"password VARCHAR(128),"+
				"name VARCHAR(200),"+
				"datamatricula VARCHAR(12),"+
				"curso VARCHAR(200),"+
				"numero VARCHAR(12),"+
				"avatar TEXT,"+
				"avaliacao TEXT)");

		db.execSQL("CREATE TABLE message (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"msgfrom VARCHAR(128)," +
				"msgtitle VARCHAR(128)," +
				"msgdate VARCHAR(12)," +
				"msgsummary TEXT)");

		db.execSQL("CREATE TABLE events (" +
				"id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
				"data VARCHAR(12)," +
				"hora VARCHAR(10)," +
				"evento VARCHAR(250))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS user");
		db.execSQL("DROP TABLE IF EXISTS events");
		db.execSQL("DROP TABLE IF EXISTS message");
	}
}

