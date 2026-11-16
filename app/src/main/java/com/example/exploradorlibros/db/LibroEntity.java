package com.example.exploradorlibros.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.exploradorlibros.models.Libro;

@Entity(tableName = "favoritos")
public class LibroEntity {
    @PrimaryKey
    public long id; // mismo ID que en el servidor
    public String titulo;
    public String autor;
    public String editorial;
    public int paginas;
    public boolean disponible;

    public static LibroEntity fromLibro(Libro l) {
        LibroEntity e = new LibroEntity();
        e.id = l.id;
        e.titulo = l.titulo;
        e.autor = l.autor;
        e.editorial = l.editorial;
        e.paginas = l.paginas;
        e.disponible = l.disponible;
        return e;
    }

    public Libro toLibro() {
        Libro l = new Libro();
        l.id = id;
        l.titulo = titulo;
        l.autor = autor;
        l.editorial = editorial;
        l.paginas = paginas;
        l.disponible = disponible;
        return l;
    }
}
