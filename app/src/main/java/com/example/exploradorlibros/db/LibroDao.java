package com.example.exploradorlibros.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface LibroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertar(LibroEntity libro);

    @Query("SELECT * FROM favoritos ORDER BY titulo")
    List<LibroEntity> obtenerTodos();

    @Query("SELECT id FROM favoritos")
    List<Long> obtenerIds();

    @Query("SELECT * FROM favoritos WHERE id = :id")
    LibroEntity obtenerPorId(long id);

    @Update
    void actualizar(LibroEntity libro);

    @Query("DELETE FROM favoritos WHERE id = :id")
    void eliminarPorId(long id);
}
