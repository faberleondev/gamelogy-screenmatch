package com.gamelogy.screenmatch.service;

import com.gamelogy.screenmatch.dto.EpisodioDTO;
import com.gamelogy.screenmatch.dto.SerieDTO;
import com.gamelogy.screenmatch.model.Categoria;
import com.gamelogy.screenmatch.principal.Serie;
import com.gamelogy.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;



@Service

public class SerieServices {
    @Autowired
    private SerieRepository repository;
    public List<SerieDTO> obtenerTodasLasSeries(){
        return convierteDatosDTO(repository.findAll());
    }

    public List<SerieDTO> obtenerTop5() {
        return convierteDatosDTO(repository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> obtenerLanzamientosMasRecientes(){
        return convierteDatosDTO(repository.lanzamientosMasRecientes());
    }



    public List<SerieDTO> convierteDatosDTO(List<Serie> serie){
        return serie.stream()
                .map(s -> new SerieDTO(
                        s.getId(),
                        s.getTitulo(),
                        s.getTotalTemporadas(),
                        s.getEvaluacion(),
                        s.getPoster(),
                        s.getGenero(),
                        s.getActores(),
                        s.getSinopsis()))
                .collect(Collectors.toList());
    }

    public SerieDTO obtenerPorId(Long id){
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(
                    s.getId(),
                    s.getTitulo(),
                    s.getTotalTemporadas(),
                    s.getEvaluacion(),
                    s.getPoster(),
                    s.getGenero(),
                    s.getActores(),
                    s.getSinopsis());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id) {
        Optional<Serie> serie = repository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroDeEpisodio()))
                    .collect(Collectors.toList());
        } return null;
    }

    public List<EpisodioDTO> obtenerTemporadasPorNumero(Long id, Long numeroTemporada){
        return repository.obtenerTemporadasPoNumero(id, numeroTemporada).stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroDeEpisodio()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> menuSeriesCategoria(String genero) {
        Categoria categoria = Categoria.fromEspaniol(genero);
        return convierteDatosDTO(repository.findByGenero(categoria));
    }

    public List<EpisodioDTO> obtenerTop5(Long id) {
        var serie = repository.findById(id).get();
        return repository.top5Episodios(serie)
                .stream()
                .map(e -> new EpisodioDTO(e.getTemporada(),e.getTitulo(),
                        e.getNumeroDeEpisodio()))
                .collect(Collectors.toList());
    }
}
