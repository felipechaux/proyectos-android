
public class Pelicula extends AppNetflix implements Visualizable {

	private int año;

	public Pelicula(String titulo, String genero, String creador, String duracion, int año) {
		super(titulo, genero, creador, duracion);
		this.año = año;
	}

	public Pelicula(String titulo, String creador) {
		super(titulo, creador);
	}

	public Pelicula() {
		this.año = 0;
	}

	/*
	 * 
	 * public Pelicula(String titulo, String genero, String creador, int año, long
	 * duracion) { this.titulo = titulo; this.genero = genero; this.creador =
	 * creador; this.año = año; this.duracion = duracion; }
	 */

	public int getAño() {
		return año;
	}

	public void setAño(int año) {
		this.año = año;
	}

	@Override
	public void marcarVisto() {
		this.visto = true;
	}

	@Override
	public boolean esVisto() {
		return this.visto;
	}

	@Override
	public String tiempoVisto() {
		return this.getDuracion();
	}

	@Override
	public String toString() {
		return "Pelicula [ Año()=" + getAño() + ", Titulo()=" + getTitulo() + ", Genero()=" + getGenero()
				+ ", Creador()=" + getCreador() + ", Duracion()=" + getDuracion() + "  Vista()= " + this.visto + "]";
	}

}
