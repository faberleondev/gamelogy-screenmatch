package com.gamelogy.screenmatch.dto;
import com.gamelogy.screenmatch.model.Categoria;

public record SerieDTO(Long id,
         String titulo,
         Integer totalTemporadas,
         Double evaluacion,
         String poster,
         Categoria genero,
         String actores,
         String sinopsis){
}
