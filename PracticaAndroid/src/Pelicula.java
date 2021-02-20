
public class Pelicula extends AppNetflix implements Visualizable {

	private int a�o;

	public Pelicula(String titulo, String genero, String creador, String duracion, int a�o) {
		super(titulo, genero, creador, duracion);
		this.a�o = a�o;
	}

	public Pelicula(String titulo, String creador) {
		super(titulo, creador);
	}

	public Pelicula() {
		this.a�o = 0;
	}

	/*
	 * 
	 * public Pelicula(String titulo, String genero, String creador, int a�o, long
	 * duracion) { this.titulo = titulo; this.genero = genero; this.creador =
	 * creador; this.a�o = a�o; this.duracion = duracion; }
	 */

	public int getA�o() {
		return a�o;
	}

	public void setA�o(int a�o) {
		this.a�o = a�o;
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
		return "Pelicula [ A�o()=" + getA�o() + ", Titulo()=" + getTitulo() + ", Genero()=" + getGenero()
				+ ", Creador()=" + getCreador() + ", Duracion()=" + getDuracion() + "  Vista()= " + this.visto + "]";
	}

}
