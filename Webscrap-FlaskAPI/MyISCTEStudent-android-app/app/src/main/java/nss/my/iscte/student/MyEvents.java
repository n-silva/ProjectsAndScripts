package nss.my.iscte.student;

public class MyEvents {

	protected long id;
	protected String data,hora,evento;

	public MyEvents() {
	}

	public MyEvents(long id, String data, String hora, String evento) {
		this.id = id;
		this.data = data;
		this.hora = hora;
		this.evento= evento;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}
}
