package com.gamelogy.screenmatch.model;

import com.gamelogy.screenmatch.principal.Serie;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
@Entity
@Table(name = "episodios")
public class Episodio {
    private Integer temporada;
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String titulo;
    private Integer numeroDeEpisodio;
    private Double evaluacion;
    private LocalDate fechaDeLanzamiento;
    @ManyToOne
    private Serie serie;

    public Episodio(){}

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroDeEpisodio() {
        return numeroDeEpisodio;
    }

    public void setNumeroDeEpisodio(Integer numeroDeEpisodio) {
        this.numeroDeEpisodio = numeroDeEpisodio;
    }

    public Double getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Double evaluacion) {
        this.evaluacion = evaluacion;
    }

    public LocalDate getFechaDeLanzamiento() {
        return fechaDeLanzamiento;
    }

    public void setFechaDeLanzamiento(LocalDate fechaDeLanzamiento) {
        this.fechaDeLanzamiento = fechaDeLanzamiento;
    }

    public Episodio(Integer numero, DatosEpisodio d){
        this.temporada = numero;
        this.titulo = d.titulo();
        this.numeroDeEpisodio = d.numeroEpisodio();
        try{
            this.evaluacion = Double.valueOf(d.evaluacion());
        } catch (NumberFormatException e){
            this.evaluacion = 0.0;
        }
        try {
            this.fechaDeLanzamiento = LocalDate.parse(d.fechaDeLanzamiento());
        } catch (DateTimeParseException e){
            this.fechaDeLanzamiento = null;
        }

    }

    @Override
    public String toString() {
        return
                " Temporada = " + temporada +
                        ", \nTítulo = '" + titulo + '\'' +
                        ", \nNumero de Episodio = " + numeroDeEpisodio +
                        ", \nEvaluación = " + evaluacion +
                        ", \nFecha de Lanzamiento = " + fechaDeLanzamiento;
    }
}