package com.gamelogy.screenmatch.principal;

import com.gamelogy.screenmatch.model.Categoria;
import com.gamelogy.screenmatch.model.DatosSerie;
import com.gamelogy.screenmatch.model.DatosTemporadas;
import com.gamelogy.screenmatch.model.Episodio;
import com.gamelogy.screenmatch.repository.SerieRepository;
import com.gamelogy.screenmatch.service.ConsumoAPI;
import com.gamelogy.screenmatch.service.ConvierteDatos;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner busqueda = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=2a8077e0";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;
    private String nombreEpisodio;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    1 - Buscar Series en OMDB
                    2 - Buscar Episodios
                    3 - Mostrar Series Buscadas
                    4 - Buscar serie guardadas por Título (en base de datos)
                    5 - Top 5 mejores Series
                    6 - Buscar Series por Categoria
                    7 - Filtrar Series
                    8 - Buscar episodios por nombre
                    9 - Topi 5 episodio mais conchesumare
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = busqueda.nextInt();
            busqueda.nextLine();

            switch (opcion){
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodioPorTitulo();
                    break;
                case 9:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción invalida");
            }
        }
    }


    //Busca los datos generales de las series
    private DatosSerie getDatosSerie(){
        System.out.println("Por favor ingresa un nombre de la serie para buscar:");
        var nombreSerie = busqueda.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ","+") + API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    // Opcion 1 del case BUSQUEDA DE SERIE
    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        //datosSeries.add(datos);
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        System.out.println(datos);
    }
    // Opcion 2 del case Busqueda EPISODIO
    private void buscarEpisodioPorSerie() {
        //DatosSerie datosSerie = getDatosSerie();
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie para encontrar el episodio");
        var nombreSerie = busqueda.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var jsonTemporada = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&Season=" +i+ API_KEY);
                var datosTemporadas = conversor.obtenerDatos(jsonTemporada, DatosTemporadas.class);
                temporadas.add(datosTemporadas);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        }

    }
    // Opcion del Case MUESTRA LAS SERIES QUE BUSCASTE
    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll(); //new ArrayList<>();
        //series = datosSeries.stream()
        //        .map(d -> new Serie(d))
        //.collect(Collectors.toList());
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
    //caso 4
    private void buscarSeriesPorTitulo(){
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = busqueda.nextLine();
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);
        if(serieBuscada.isPresent()){
            System.out.println("La encontramos: " + serieBuscada.get());
        } else {
            System.out.println("No se encontraron resultados.");
        }
    }
    // caso 5
    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s ->
                System.out.println(
                        "Serie: " + s.getTitulo() +
                                "\nEvaluacion: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategoria(){
        System.out.println("Coloca la categoria para organizar las series: ");
        var genero = busqueda.nextLine();
        var categoria = Categoria.fromString(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Resultado: " + genero);
        seriesPorCategoria.forEach(System.out::println);

    }

    private void filtrarSeriesPorTemporadaYEvaluacion(){
        System.out.println("Series de cuantas temporadas?: ");
        var totalTemporadas = busqueda.nextInt();
        busqueda.nextLine();
        System.out.println("A partir de que puntaje..?: ");
        var evaluacion = busqueda.nextDouble();
        busqueda.nextLine();
        List<Serie> filtrosSeries = repositorio.seriesPorTemporadaYEvaluacion(totalTemporadas, evaluacion);
        System.out.println("*** Series Filtradas ***");
        filtrosSeries.forEach(s ->
                System.out.println(s.getTitulo() + " - puntaje: " + s.getEvaluacion()));
    }

    private void buscarEpisodioPorTitulo(){
        System.out.println("Escribe el nombre del episodio que deseas encontrar");
        nombreEpisodio = busqueda.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s Episodio: %s Temporada: %s Evaluacion: %s ",
                        e.getSerie(),
                        e.getNumeroDeEpisodio(),
                        e.getTemporada(),
                        e.getEvaluacion()));
    }
    private void buscarTop5Episodios(){
        buscarSeriesPorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.episodiosPorNombre(nombreEpisodio);
            topEpisodios.forEach(e ->
                    System.out.printf("Episodio: %s Nº: %s Temporada: %s Evaluacion: %s\n",
                            e.getTitulo(),
                            e.getNumeroDeEpisodio(),
                            e.getTemporada(),
                            e.getEvaluacion()));
        }
    }
//
////        for (int i = 0; i <datos.totalDeTemporadas() ; i++) {
////            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodio();
////            for (int j = 0; j < episodiosTemporada.size(); j++) {
////                System.out.println(episodiosTemporada.get(j).titulo());
////            }
////        }
//        //Mostrar Solo el titulo de los episodios para las temporadas. FUNCION LAMBDA
//        //temporadas.forEach(t -> t.episodio().forEach(e -> System.out.println(e.titulo())));
//
//        //Convertir todas las informaciones a una lista del tipo DatosEpisodio
//
//        List<DatosEpisodio> datosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodio().stream())
//                .collect(Collectors.toList());
//
//        // Top 5 episodios mas conchesumare
////        System.out.println("Topi 5 episode mas conchesumare");
////        datosEpisodios.stream()
////                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
////                //.peek(e -> System.out.println("1er paso filtro (N/A)" + e))
////                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
////                //.peek(e -> System.out.println("2do paso ordenación (n1 < n2)" + e))
////                //.map(e -> e.titulo().toUpperCase())
////                //.peek(e -> System.out.println("3er paso filtro Mayuscula (n -> N)" + e))
////                .limit(5)
////                .forEach(System.out::println);
//
//        // Convirtiendo los datos a una lista de tipo episodio
//
//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t -> t.episodio().stream()
//                        .map(d -> new Episodio(t.numeroTemporada(),d)))
//                .collect(Collectors.toList());
//
//        //episodios.forEach(System.out::println);
//
//        // Busqueda de episodios a partir de X año
////        System.out.println("Indica el año desde donde quieres ver: ");
////        var fecha = busqueda.nextInt();
////        busqueda.nextLine();
////
////        LocalDate fechaBusqueda = LocalDate.of(fecha, 1,1);
////
////        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
////        episodios.stream()
////                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
////                .forEach(e -> {
////                    System.out.println(
////                            "Temporada "  + e.getTemporada() +
////                                    " Episodio " + e.getTitulo() +
////                                    " Fecha " + e.getFechaDeLanzamiento().format(dtf));
////                });
//
//        // Busca episodios por fragmentos de titulo (contiene X palabra)
////        System.out.println("Recuerdas el nombre del episodios? Bueno puedes escribirlo aquí, aunque sea una parte");
////        var fragmentoTitulo = busqueda.nextLine();
////        Optional<Episodio> episodioBuscar = episodios.stream()
////                .filter(e -> e.getTitulo().toUpperCase().contains(fragmentoTitulo.toUpperCase()))
////                .findFirst();
////        if (episodioBuscar.isPresent()){
////            System.out.println("Episodio encontrado: " + episodioBuscar.get());
////        } else {
////            System.out.println("No se encontro ningun resultado.");
////        }
//        Map<Integer, Double> evaluacionPorTemporada = episodios.stream()
//                .filter(e -> e.getEvaluacion() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getEvaluacion)));
//        System.out.println(evaluacionPorTemporada);
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getEvaluacion() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
//        System.out.println("Media de las evaluaciones " + est.getAverage());
//        System.out.println("Episodio mejor puntuado " + est.getMax());
//        System.out.println("Episodio más bodrío " + est.getMin());

}