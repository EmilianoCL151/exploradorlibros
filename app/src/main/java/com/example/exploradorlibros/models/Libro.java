package com.example.exploradorlibros.models;

/** Mapea EntityModelLibroDto. El campo _links se ignora (Gson omite lo no declarado). */
public class Libro {
    public long id;
    public String titulo;
    public String autor;
    public String editorial;
    public int paginas;
    public boolean disponible;
}
