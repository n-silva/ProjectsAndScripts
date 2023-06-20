package nss.my.iscte.student;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;



public class MyISCTEDatasource {
	protected SQLiteDatabase db;
	protected DatabaseHelper dbhelper;

	public MyISCTEDatasource(Context c) {
		dbhelper = new DatabaseHelper(c);
	}

	public void open() {
		db = dbhelper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	//CRUD
    //========================= START USER ============================================
	// CREATE NEW User
	public User createUser(String email, String password, String name, String datamatricula, String curso, String numero, String avatar, String avaliacao) {
		ContentValues values = new ContentValues();
		values.put("email", email);
		values.put("password", password);
		values.put("name", name);
		values.put("datamatricula", datamatricula);
		values.put("curso", curso);
		values.put("numero", numero);
		values.put("avatar", avatar);
		values.put("avaliacao", avaliacao);

		long lastId = db.insert("user",null, values);

		return new User(lastId, email, password, name, datamatricula, curso, numero, avatar, avaliacao);
	}

	// OBTER UM USER
	public User getUser(long id) {
		Cursor c = db.rawQuery("SELECT * FROM user WHERE id = " + id, null);

		if(c.getCount() == 0) {
			return null;
		} else {
			c.moveToFirst();
			User ct = new User(id,
					c.getString(1),
					c.getString(2),
					c.getString(3),
					c.getString(4),
					c.getString(5),
					c.getString(6),
					c.getString(7),
					c.getString(8));
			c.close();
			return ct;
		}
	}

	// OBTER UM USER
	public User getUserByEmail(String email) {
		Cursor c = db.rawQuery("SELECT * FROM user WHERE email = '" + email + "'", null);
		if(c.getCount() == 0) {
			return null;
		} else {
			c.moveToFirst();
			User ct = new User(c.getLong(0),
					c.getString(1),
					c.getString(2),
					c.getString(3),
					c.getString(4),
					c.getString(5),
					c.getString(6),
					c.getString(7),
					c.getString(8));
			c.close();
			return ct;
		}
	}

	// OBTER UM USER
	public Boolean UserExits(String email) {
		Cursor c = db.rawQuery("SELECT * FROM user WHERE email = '" + email + "'", null);
		if(c.getCount() == 0) {
			return false;
		} else {
			c.close();
			return true;
		}
	}

	// OBTER TODOS OS Users
	public List<User> getAllUsers() {
		Cursor c = db.rawQuery("SELECT * FROM user", null);

		if(c.getCount() == 0) {
			return null;
		} else {
			c.moveToFirst();
			List<User> userList = new ArrayList<User>();
			while(!c.isAfterLast()) {
				userList.add(new User(c.getLong(0), c.getString(1), c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getString(6),c.getString(7),c.getString(8)));
				c.moveToNext();
			}
			c.close();
			return userList;
		}
	}

	// APAGAR USER
	public void apagarUser(long id) {
		if (id == -1) {
			int result = db.delete("user",null,null);
		}else{
			int result = db.delete("user", "id=?", new String[] { String.valueOf(id) });
		}
	}

	// CONTAR REGISTOS
	public int countUser(){
		Cursor c = db.rawQuery("SELECT * FROM user", null);
		return c.getCount();
	}
	//========================= END USER ============================================


	//=============================== START MESSAGE =====================================
	// CREATE NEW Message
	public Message createMessage(String sFrom, String sSubject, String sDate, String sMsg) {
		ContentValues values = new ContentValues();
		values.put("msgfrom", sFrom);
		values.put("msgtitle", sSubject);
		values.put("msgdate", sDate);
		values.put("msgsummary", sMsg);

		long lastId = db.insert("message",null, values);

		return new Message(lastId, sFrom, sSubject, sDate, sMsg);
	}

	public Message getMessage(long id) {
		Cursor c = db.rawQuery("SELECT * FROM message WHERE id = " + id, null);
		if(c.getCount() == 0) {
			return null;
		} else {
			c.moveToFirst();
			Message ct = new Message(id,
					c.getString(1),
					c.getString(2),
					c.getString(3),
					c.getString(4));
			c.close();
			return ct;
		}
	}

	public Message getUserBySender(String sFrom) {
		Cursor c = db.rawQuery("SELECT * FROM message WHERE msgfrom = '" + sFrom + "'", null);
		if(c.getCount() == 0) {
			return null;
		} else {
			c.moveToFirst();
			Message ct = new Message(c.getLong(0),
					c.getString(1),
					c.getString(2),
					c.getString(3),
					c.getString(4));
			c.close();
			return ct;
		}
	}

	public Boolean MessageExits(String sFrom,String sSubject,String sDate) {
		Cursor c = db.rawQuery("SELECT * FROM message WHERE msgfrom = '" + sFrom + "' and msgtitle = '" + sSubject + "' and msgdate = '"+ sDate + "';", null);

		if(c.getCount() == 0) {
			return false;
		} else {
			c.close();
			return true;
		}
	}

	public List<Message> getAllMessages() {
		Cursor c = db.rawQuery("SELECT * FROM message", null);

		if(c.getCount() == 0) {
			return null;
		} else {
			c.moveToFirst();
			List<Message> messageList = new ArrayList<Message>();
			while(!c.isAfterLast()) {
				messageList.add(new Message(c.getLong(0), c.getString(1), c.getString(2),c.getString(3),c.getString(4)));
				c.moveToNext();
			}
			c.close();
			return messageList;
		}
	}

	public void apagarMessage(long id) {
		if (countUser() > 0) {
			if (id == -1) {
				int result = db.delete("message", null, null);
			} else {
				int result = db.delete("message", "id=?", new String[]{String.valueOf(id)});
			}
		}
	}

	public int countMessage(){
		Cursor c = db.rawQuery("SELECT * FROM message", null);
		return c.getCount();
	}

	//================================================ EVENTS ==================================
	// CREATE NEW Event
	public MyEvents createEvent(String data, String hora, String evento) {
		ContentValues values = new ContentValues();
		values.put("data", data);
		values.put("hora", hora);
		values.put("evento", evento);

		long lastId = db.insert("events",null, values);
		return new MyEvents(lastId,data, hora, evento);
	}

	public MyEvents getEvent(long id) {
		Cursor c = db.rawQuery("SELECT * FROM events WHERE id = " + id, null);
		if(c.getCount() == 0) {
			return null;
		} else {
			c.moveToFirst();
			MyEvents ct = new MyEvents(id,
					c.getString(1),
					c.getString(2),
					c.getString(3));
			c.close();
			return ct;
		}
	}

	public MyEvents getEventByDate(String date) {
		Cursor c = db.rawQuery("SELECT * FROM events WHERE data = '" + date + "'", null);
		if(c.getCount() == 0) {
			return null;
		} else {
			c.moveToFirst();
			MyEvents ct = new MyEvents(c.getLong(0),
					c.getString(1),
					c.getString(2),
					c.getString(3));
			c.close();
			return ct;
		}
	}

	public List<MyEvents> getAllEvents() {
		Cursor c = db.rawQuery("SELECT * FROM events", null);

		if(c.getCount() == 0) {
			return null;
		} else {
			c.moveToFirst();
			List<MyEvents> eventList = new ArrayList<MyEvents>();
			while(!c.isAfterLast()) {
				eventList.add(new MyEvents(c.getLong(0), c.getString(1), c.getString(2),c.getString(3)));
				c.moveToNext();
			}
			c.close();
			return eventList;
		}
	}

	public void delEvent(long id) {
		if (countUser() > 0) {
			if (id == -1) {
				int result = db.delete("events", null, null);
			} else {
				int result = db.delete("events", "id=?", new String[]{String.valueOf(id)});
			}
		}
	}

	public int countEvents(){
		Cursor c = db.rawQuery("SELECT * FROM events", null);
		return c.getCount();
	}
}


