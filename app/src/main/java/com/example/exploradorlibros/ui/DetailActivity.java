package com.example.exploradorlibros.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.exploradorlibros.api.RetrofitClient;
import com.example.exploradorlibros.databinding.ActivityDetailBinding;
import com.example.exploradorlibros.db.AppDatabase;
import com.example.exploradorlibros.db.LibroDao;
import com.example.exploradorlibros.db.LibroEntity;
import com.example.exploradorlibros.models.Libro;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "libro_id";

    private ActivityDetailBinding b;
    private long id;
    private Libro libro;
    private boolean esFavorito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        id = getIntent().getLongExtra(EXTRA_ID, -1);
        b.btnFavorito.setEnabled(false);
        b.btnFavorito.setOnClickListener(v -> toggleFavorito());
        cargarDetalle();
    }

    private void cargarDetalle() {
        b.progress.setVisibility(View.VISIBLE);
        RetrofitClient.getApi().getLibro(id).enqueue(new Callback<Libro>() {
            @Override
            public void onResponse(Call<Libro> call, Response<Libro> resp) {
                if (isFinishing() || isDestroyed()) return;
                if (resp.isSuccessful() && resp.body() != null) {
                    b.progress.setVisibility(View.GONE);
                    libro = resp.body();
                    mostrar();
                    sincronizarFavorito();
                } else {
                    respaldoLocal("Error del servidor (código " + resp.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Libro> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                respaldoLocal("Error de conexión");
            }
        });
    }

    /** Sin red: si el libro es favorito, se muestra la copia guardada en Room. */
    private void respaldoLocal(String motivo) {
        AppDatabase.IO.execute(() -> {
            LibroEntity e = AppDatabase.getInstance(this).libroDao().obtenerPorId(id);
            runOnUiThread(() -> {
                b.progress.setVisibility(View.GONE);
                if (e != null) {
                    libro = e.toLibro();
                    esFavorito = true;
                    mostrar();
                    actualizarBoton();
                    Toast.makeText(this, motivo + ". Mostrando copia local.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, motivo, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /** Detecta si ya es favorito y, de serlo, refresca la copia local con los datos del servidor. */
    private void sincronizarFavorito() {
        final Libro actual = libro;
        AppDatabase.IO.execute(() -> {
            LibroDao dao = AppDatabase.getInstance(this).libroDao();
            boolean fav = dao.obtenerPorId(id) != null;
            if (fav) dao.actualizar(LibroEntity.fromLibro(actual));
            runOnUiThread(() -> {
                esFavorito = fav;
                actualizarBoton();
            });
        });
    }

    private void toggleFavorito() {
        if (libro == null) return;
        final boolean eraFav = esFavorito;
        final Libro actual = libro;
        AppDatabase.IO.execute(() -> {
            LibroDao dao = AppDatabase.getInstance(this).libroDao();
            if (eraFav) dao.eliminarPorId(actual.id); else dao.insertar(LibroEntity.fromLibro(actual));
            runOnUiThread(() -> {
                esFavorito = !eraFav;
                actualizarBoton();
                Toast.makeText(this, eraFav ? "Quitado de favoritos" : "Agregado a favoritos",
                        Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void mostrar() {
        b.tvId.setText("ID: " + libro.id);
        b.tvTitulo.setText(libro.titulo);
        b.tvAutor.setText("Autor: " + libro.autor);
        b.tvEditorial.setText("Editorial: " + libro.editorial);
        b.tvPaginas.setText("Páginas: " + libro.paginas);
        b.tvDisponible.setText("Disponibilidad: " + (libro.disponible ? "Disponible" : "No disponible"));
    }

    private void actualizarBoton() {
        b.btnFavorito.setEnabled(libro != null);
        b.tvEstadoFav.setText(esFavorito ? "★ Está en tus favoritos" : "☆ No está en favoritos");
        b.btnFavorito.setText(esFavorito ? "Quitar de favoritos" : "Agregar a favoritos");
    }
}
