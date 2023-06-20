package nss.my.iscte.student;

public class User {

	protected long id;
	protected String email, password, name, datamatricula, curso, numero, avatar, avaliacao;

	public User() {
	}

	public User(long id, String email, String password, String name, String datamatricula, String curso, String numero, String avatar, String avaliacao) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.datamatricula = datamatricula;
		this.curso= curso;
		this.numero = numero;
		this.avatar = avatar;
		this.avaliacao = avaliacao;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatamatricula() {
		return datamatricula;
	}

	public void setDatamatricula(String datamatricula) {
		this.datamatricula = datamatricula;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(String avaliacao) {
		this.avaliacao= avaliacao;
	}

}
