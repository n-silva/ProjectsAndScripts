package nss.my.iscte.student;

public class Contact {
	protected long id;
	protected  String name, email, type, avatar;

	public Contact() {
	}

	public Contact(long id, String name, String email, String type, String avatar) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.type = type;
		this.avatar = avatar;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
