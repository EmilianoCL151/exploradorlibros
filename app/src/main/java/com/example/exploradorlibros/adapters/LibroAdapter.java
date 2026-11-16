package com.example.exploradorlibros.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exploradorlibros.databinding.ItemLibroBinding;
import com.example.exploradorlibros.models.Libro;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LibroAdapter extends RecyclerView.Adapter<LibroAdapter.VH> {

    public interface Listener {
        void onDetalle(Libro libro);
        void onToggleFavorito(Libro libro);
    }

    private final boolean modoFavoritos;
    private final Listener listener;
    private final List<Libro> libros = new ArrayList<>();
    private final Set<Long> favoritos = new HashSet<>();

    public LibroAdapter(boolean modoFavoritos, Listener listener) {
        this.modoFavoritos = modoFavoritos;
        this.listener = listener;
    }

    public void setLibros(List<Libro> nuevos) {
        libros.clear();
        libros.addAll(nuevos);
        notifyDataSetChanged();
    }

    public void setFavoritos(Set<Long> ids) {
        favoritos.clear();
        favoritos.addAll(ids);
        notifyDataSetChanged();
    }

    public void setFavorito(long id, boolean esFav) {
        if (esFav) favoritos.add(id); else favoritos.remove(id);
        notifyDataSetChanged();
    }

    public boolean esFavorito(long id) { return favoritos.contains(id); }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemLibroBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Libro l = libros.get(position);
        ItemLibroBinding b = h.b;
        b.tvId.setText("ID: " + l.id);
        b.tvTitulo.setText(l.titulo);
        b.tvAutor.setText("Autor: " + l.autor);
        b.tvEditorial.setText("Editorial: " + l.editorial);
        b.tvPaginas.setText("Páginas: " + l.paginas);
        b.tvDisponible.setText("Disponibilidad: " + (l.disponible ? "Disponible" : "No disponible"));

        if (modoFavoritos) {
            b.btnFavorito.setText("Eliminar");
        } else {
            b.btnFavorito.setText(favoritos.contains(l.id) ? "★ Quitar" : "☆ Favorito");
        }
        b.btnDetalle.setOnClickListener(v -> listener.onDetalle(l));
        b.btnFavorito.setOnClickListener(v -> listener.onToggleFavorito(l));
    }

    @Override
    public int getItemCount() { return libros.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ItemLibroBinding b;
        VH(ItemLibroBinding b) { super(b.getRoot()); this.b = b; }
    }
}
