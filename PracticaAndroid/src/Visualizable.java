
public interface Visualizable {
	
	
	//cambia el atributo de visto a true.
	public void  marcarVisto();

	//devuelve el estado del atributo visto.
	public boolean esVisto();
	
	//devuelve el tiempo en minutos/segundos que se visualizó el video.
	public String tiempoVisto ();
}
