
public class AppNetflix {

	private String titulo, genero, creador;

	public boolean visto;

	private String duracion;

	public AppNetflix() {
		this.genero = null;
		this.visto = false;
		this.duracion = "0:0";
	}
	

	public AppNetflix(String titulo, String creador) {
		this.titulo = titulo;
		this.creador = creador;
	}

	public AppNetflix(String titulo, String genero, String creador, String duracion) {
		this.titulo = titulo;
		this.genero = genero;
		this.creador = creador;
		this.duracion = duracion;
	}


	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getCreador() {
		return creador;
	}

	public void setCreador(String creador) {
		this.creador = creador;
	}

	public String getDuracion() {
		return duracion;
	}

	public void setDuracion(String duracion) {
		this.duracion = duracion;
	}

	
}
