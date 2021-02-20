import java.util.ArrayList;
import java.util.Iterator;

public class EjecutarNetflix {

	public static void main(String[] args) {

		/// Peliculas///
		ArrayList<Pelicula> peliculas = new ArrayList<Pelicula>();
		Pelicula peliUno = new Pelicula("Wonder woman", "Action", "Patty Jenkins", "25:30", 2020);
		peliUno.marcarVisto();
		peliculas.add(peliUno);
		Pelicula peliDos = new Pelicula("InterStellar", "Jhons hack");
		peliculas.add(peliDos);
		peliculas.add(new Pelicula("Epic movie", "Comedia"));
		Pelicula peliCuatro = new Pelicula("The Walking dead", "Horror", "Alan jobd", "15:05", 2010);
		peliCuatro.marcarVisto();
		peliculas.add(peliCuatro);

		peliculas.add(new Pelicula("Epic movie", "Horror", "Alan jobd", "20:00", 2018));

		// System.out.println(peliculas);

		/// Series///
		ArrayList<Serie> series = new ArrayList<Serie>();
		Serie serieUno = new Serie("Mr robot", "Drama", "Alan jod", "60:00");
		serieUno.marcarVisto();
		serieUno.setNoTemporadas(10);
		series.add(serieUno);
		series.add(new Serie("Bounding", "Patty Jenkins"));
		Serie serieTres = new Serie("La maldicion de hill house", "Horror", "Mike Flanagan", "120:00");
		serieTres.marcarVisto();
		serieTres.setNoTemporadas(2);
		series.add(serieTres);
		Serie serieCuatro = new Serie("La bruja", "Horror", "Alan jobd", "30:00");
		serieCuatro.marcarVisto();
		serieCuatro.setNoTemporadas(16);
		series.add(serieCuatro);
		series.add(new Serie("Epic movie", "Comedia"));

		// System.out.println(series);

		// 1) Muestra una lista de las Películas y Series que se visualizaron
		// y un detalle de los minutos/segundos visualizados
		System.out.println("Series Visualizadas //// ");
		for (Serie serie : series) {

			if (serie.esVisto()) {
				System.out.println("Serie -> " + serie.getTitulo() + " - Tiempo visualizado : " + serie.tiempoVisto());
			}
		}
		System.out.println();
		System.out.println("Peliculas Visualizadas //// ");
		for (Pelicula peli : peliculas) {

			if (peli.esVisto()) {
				System.out.println("Pelicula -> " + peli.getTitulo() + " - Tiempo visualizado : " + peli.tiempoVisto());
			}
		}

		System.out.println();
		// 2) Por último, indica la serie con más temporadas
		// y la película del año más reciente. Muéstralos en pantalla
		// con toda su información (usa el método toString()).

		//
	    
		int masTemporadas=series.get(0).getNoTemporadas();
		for (int i = 0; i < series.size(); i++) {
			
			Serie serie = series.get(i);
			
			if(serie.getNoTemporadas() > masTemporadas){
				System.out.println(serie.getNoTemporadas()+" > "+ masTemporadas);
				masTemporadas = serie.getNoTemporadas();
			}else {
				System.out.println("no "+serie.getNoTemporadas()+" > "+ masTemporadas);
			}
						
			//System.out.println("Serie -> "+serie.getTitulo()+" : No Temporadas : "+serie.getNoTemporadas());
			
		}
		System.out.println("mas temporadas "+masTemporadas);

	}

}
