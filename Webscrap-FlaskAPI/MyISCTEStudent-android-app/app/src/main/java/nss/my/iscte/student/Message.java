package nss.my.iscte.student;

public class Message {
	protected long id;
	protected  String sFrom, sSubject, sDate, sMsg;

	public Message() {
	}

	public Message(long id, String sFrom, String sSubject, String sDate, String sMsg) {
		this.id = id;
		this.sFrom = sFrom;
		this.sSubject = sSubject;
		this.sDate = sDate;
		this.sMsg = sMsg;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getsFrom() {
		return sFrom;
	}

	public void setsFrom(String sFrom) {
		this.sFrom = sFrom;
	}

	public String getsSubject() {
		return sSubject;
	}

	public void setsSubject(String sSubject) {
		this.sSubject = sSubject;
	}

	public String getsDate() {
		return sDate;
	}

	public void setsDate(String sDate) {
		this.sDate = sDate;
	}

	public String getsMsg() {
		return sMsg;
	}

	public void setsMsg(String sMsg) {
		this.sMsg = sMsg;
	}
}
