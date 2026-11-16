package com.example.exploradorlibros.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.exploradorlibros.adapters.LibroAdapter;
import com.example.exploradorlibros.api.LibroParser;
import com.example.exploradorlibros.api.RetrofitClient;
import com.example.exploradorlibros.databinding.ActivityCatalogBinding;
import com.example.exploradorlibros.db.AppDatabase;
import com.example.exploradorlibros.db.LibroEntity;
import com.example.exploradorlibros.models.Libro;
import com.google.gson.JsonElement;
import java.util.HashSet;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogActivity extends AppCompatActivity implements LibroAdapter.Listener {
    private ActivityCatalogBinding b;
    private LibroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityCatalogBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        new PreferencesManager(this).setUltimaSeccion(PreferencesManager.SECCION_CATALOGO);

        adapter = new LibroAdapter(false, this);
        b.rvLibros.setLayoutManager(new LinearLayoutManager(this));
        b.rvLibros.setAdapter(adapter);
        b.btnActualizar.setOnClickListener(v -> cargarCatalogo());
        cargarCatalogo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Al volver del detalle, refresca qué libros son favoritos (Room, hilo secundario).
        AppDatabase.IO.execute(() -> {
            List<Long> ids = AppDatabase.getInstance(this).libroDao().obtenerIds();
            runOnUiThread(() -> adapter.setFavoritos(new HashSet<>(ids)));
        });
    }

    private void cargarCatalogo() {
        b.progress.setVisibility(View.VISIBLE);
        b.btnActualizar.setEnabled(false);
        RetrofitClient.getApi().getLibros().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> resp) {
                if (isFinishing() || isDestroyed()) return;
                terminarCarga();
                if (!resp.isSuccessful() || resp.body() == null) {
                    toast("Error del servidor (código " + resp.code() + ")");
                    return;
                }
                List<Libro> lista = LibroParser.parse(resp.body());
                adapter.setLibros(lista);
                if (lista.isEmpty()) toast("El catálogo está vacío");
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                terminarCarga();
                toast("Error de conexión. Revisa tu Internet e intenta de nuevo.");
            }
        });
    }

    private void terminarCarga() {
        b.progress.setVisibility(View.GONE);
        b.btnActualizar.setEnabled(true);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDetalle(Libro libro) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailActivity.EXTRA_ID, libro.id);
        startActivity(i);
    }

    @Override
    public void onToggleFavorito(Libro libro) {
        boolean eraFav = adapter.esFavorito(libro.id);
        AppDatabase.IO.execute(() -> {
            if (eraFav) AppDatabase.getInstance(this).libroDao().eliminarPorId(libro.id);
            else AppDatabase.getInstance(this).libroDao().insertar(LibroEntity.fromLibro(libro));
            runOnUiThread(() -> {
                adapter.setFavorito(libro.id, !eraFav);
                toast(eraFav ? "Quitado de favoritos" : "Agregado a favoritos");
            });
        });
    }
}
