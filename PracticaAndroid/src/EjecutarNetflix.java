import java.util.ArrayList;

public class EjecutarNetflix {
	static ArrayList<Pelicula> peliculas = new ArrayList<Pelicula>();
	static ArrayList<Serie> series = new ArrayList<Serie>();

	static int masTemporadas = 0;
	static Serie serieObjetivo = null;
	static boolean esSerieUno = true;

	static int peliMasReciente = 0;
	static Pelicula peliculaObjetivo = null;
	static boolean esPeliculaUno = true;

	public static void main(String[] args) {
		
		//para provocar IndexOutOfBoundsException agregar el metodo reporteSeriesPeliculas() de primeras
		//reporteSeriesPeliculas();

		/// Peliculas///
		crearPeliculas();

		/// Series///
		crearSeries();

		// 1) Muestra una lista de las Peliculas y Series que se visualizaron
		// y un detalle de los minutos/segundos visualizados
		reporteSeriesPeliculas();

		// 2) Indica la serie con mas temporadas
		// y la pelacula del ańo mas reciente. Muestralos en pantalla
		// con toda su informacion (usa el metodo toString()).

		try {
			System.out.println("******** Serie con mas temporadas *******");
			if (esSerieUno) {
				// para provocar un IndexOutOfBoundsException - agregale indice mayor a 4 -
				// series.get(5)
				System.out.println(series.get(0));
			} else {
				System.out.println(serieObjetivo);
			}
			System.out.println();
		} catch (IndexOutOfBoundsException o) {
			System.out.println("Error de indice - Estas agregando un indice erroreno en la coleccion de series");
			o.printStackTrace();
		} catch (Exception e) {
			System.out.println("Ocurrio un error al intentar mostrar la serie con mas  mas temporadas");
			e.printStackTrace();
		} finally {
			System.out.println();
			System.out.println(" **************************************");
		}

		////////////////

		try {

			System.out.println("******** Pelicula mas reciente *******");
			if (esPeliculaUno) {
				// para provocar un IndexOutOfBoundsException - agregar indice mayor a 4 -
				// peliculas.get(5)
				System.out.println(peliculas.get(0));
			} else {
				System.out.println(peliculaObjetivo);
			}

		} catch (IndexOutOfBoundsException o) {
			System.out.println("Error de indice - Estas agregando un indice erroreno en la coleccion de peliculas");
			o.printStackTrace();
		} catch (Exception e) {
			System.out.println("Ocurrio un error al intentar mostrar la pelicula mas reciente");
			e.printStackTrace();
		} finally {
			System.out.println();
			System.out.println(" **************************************");
		}

	}

	private static void reporteSeriesPeliculas() {

		try {
			masTemporadas = series.get(0).getNoTemporadas();
			peliMasReciente = peliculas.get(0).getAño();

			System.out.println("Series Visualizadas ***** ");
			for (Serie serie : series) {
				if (serie.esVisto()) {
					System.out.println(
							"Serie -> " + serie.getTitulo() + " - Tiempo visualizado : " + serie.tiempoVisto());
				}
				////////////
				if (serie.getNoTemporadas() > masTemporadas) {
					masTemporadas = serie.getNoTemporadas();
					serieObjetivo = serie;
					esSerieUno = false;
				}
			}
			System.out.println();
			System.out.println("Peliculas Visualizadas ***** ");
			for (Pelicula peli : peliculas) {

				if (peli.esVisto()) {
					System.out.println(
							"Pelicula -> " + peli.getTitulo() + " - Tiempo visualizado : " + peli.tiempoVisto());
				}
				/////////
				if (peli.getAño() > peliMasReciente) {
					peliMasReciente = peli.getAño();
					peliculaObjetivo = peli;
					esPeliculaUno = false;
				}

			}
			System.out.println();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Error al inicializar - no sean creado las colecciones");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Ocurrio un error al crear el reporte de pelicuas/series visualizadas");
			e.printStackTrace();
		}
	}

	private static void crearSeries() {
		try {
			Serie serieUno = new Serie("Mr robot", "Drama", "Alan jod", "60:00");
			serieUno.marcarVisto();
			serieUno.setNoTemporadas(5);
			series.add(serieUno);
			series.add(new Serie("Bounding", "Patty Jenkins"));
			Serie serieTres = new Serie("La maldicion de hill house", "Horror", "Mike Flanagan", "120:00");
			serieTres.marcarVisto();
			serieTres.setNoTemporadas(10);
			series.add(serieTres);
			Serie serieCuatro = new Serie("La bruja", "Horror", "Alan jobd", "30:00");
			serieCuatro.marcarVisto();
			serieCuatro.setNoTemporadas(16);
			series.add(serieCuatro);
			series.add(new Serie("Epic movie", "Comedia"));
		} catch (Exception e) {
			System.out.println("Ocurrio un error al creear tus series");
			e.printStackTrace();
		}
	}

	private static void crearPeliculas() {
		try {
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
		} catch (Exception e) {
			System.out.println("Ocurrio un error al creear tus peliculas");
			e.printStackTrace();
		}

	}

}
