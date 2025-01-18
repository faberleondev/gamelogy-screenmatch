package com.gamelogy.screenmatch.model;

public enum Categoria {
    ACCION("Action", "Acción"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    AVENTURA("Adventure", "Aventura"),
    TERROR("Horror", "Terror"),
    SUSPENSO("Suspense", "Suspenso"),
    MISTERIO("Mistery", "Misterio"),
    CRIMEN("Crime", "Crimen"),
    BIOGRAFIA("Biography", "Biografia");

    private String categoriaIngles;
    private String categoriaEspaniol;
    Categoria (String categoriaIngles, String categoriaEspaniol){
        this.categoriaIngles = categoriaIngles;
        this.categoriaEspaniol = categoriaEspaniol;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaIngles.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

    public static Categoria fromEspaniol(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaEspaniol.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }

}
