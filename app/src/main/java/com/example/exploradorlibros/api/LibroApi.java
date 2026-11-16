package com.example.exploradorlibros.api;

import com.example.exploradorlibros.models.Libro;
import com.google.gson.JsonElement;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LibroApi {
    // JsonElement: la lista puede venir como arreglo plano o en formato HAL (_embedded).
    @GET("api/v1/libros")
    Call<JsonElement> getLibros();

    @GET("api/v1/libros/{id}")
    Call<Libro> getLibro(@Path("id") long id);
}
