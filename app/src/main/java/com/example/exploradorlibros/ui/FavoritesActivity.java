package com.example.exploradorlibros.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.exploradorlibros.adapters.LibroAdapter;
import com.example.exploradorlibros.databinding.ActivityFavoritesBinding;
import com.example.exploradorlibros.db.AppDatabase;
import com.example.exploradorlibros.db.LibroEntity;
import com.example.exploradorlibros.models.Libro;
import java.util.ArrayList;
import java.util.List;

/** Lee únicamente de Room: funciona sin Internet. */
public class FavoritesActivity extends AppCompatActivity implements LibroAdapter.Listener {
    private ActivityFavoritesBinding b;
    private LibroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        new PreferencesManager(this).setUltimaSeccion(PreferencesManager.SECCION_FAVORITOS);

        adapter = new LibroAdapter(true, this);
        b.rvFavoritos.setLayoutManager(new LinearLayoutManager(this));
        b.rvFavoritos.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarFavoritos();
    }

    private void cargarFavoritos() {
        AppDatabase.IO.execute(() -> {
            List<LibroEntity> entidades = AppDatabase.getInstance(this).libroDao().obtenerTodos();
            List<Libro> libros = new ArrayList<>();
            for (LibroEntity e : entidades) libros.add(e.toLibro());
            runOnUiThread(() -> {
                adapter.setLibros(libros);
                b.tvVacio.setVisibility(libros.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    @Override
    public void onDetalle(Libro libro) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailActivity.EXTRA_ID, libro.id);
        startActivity(i);
    }

    @Override
    public void onToggleFavorito(Libro libro) { // en esta pantalla siempre elimina
        AppDatabase.IO.execute(() -> {
            AppDatabase.getInstance(this).libroDao().eliminarPorId(libro.id);
            runOnUiThread(() -> Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show());
            cargarFavoritos();
        });
    }
}
