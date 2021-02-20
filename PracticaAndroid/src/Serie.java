
public class Serie extends AppNetflix implements Visualizable {

	private int noTemporadas;

	public Serie(String titulo, String genero, String creador, String duracion) {
		super(titulo, genero, creador, duracion);
		this.noTemporadas = 1;
	}

	public Serie(String titulo, String creador) {
		super(titulo, creador);
	}

	public Serie() {

	}

	/*
	 * public Serie(String titulo, String creador, int noTemporadas, int duracion) {
	 * this.titulo = titulo; this.creador = creador; this.noTemporadas =
	 * noTemporadas; this.duracion = duracion; }
	 */

	public int getNoTemporadas() {
		return noTemporadas;
	}

	public void setNoTemporadas(int noTemporadas) {
		this.noTemporadas = noTemporadas;
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
		return "Serie [NoTemporadas()=" + getNoTemporadas() + ", Titulo()=" + getTitulo() + ", Genero()=" + getGenero()
				+ ", Creador()=" + getCreador() + ", Duracion()=" + getDuracion() + " Vista()= " + this.visto + " ]";
	}

}
