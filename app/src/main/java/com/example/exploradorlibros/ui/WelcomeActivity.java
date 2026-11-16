package com.example.exploradorlibros.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.exploradorlibros.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding b;
    private PreferencesManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        prefs = new PreferencesManager(this);

        b.etNombre.setText(prefs.getNombre());
        b.switchTema.setChecked(prefs.isTemaOscuro());
        b.switchTema.setOnCheckedChangeListener((v, oscuro) -> {
            prefs.setTemaOscuro(oscuro);
            PreferencesManager.aplicarTema(oscuro); // recrea la Activity con el nuevo tema
        });

        b.btnGuardar.setOnClickListener(v -> {
            String nombre = b.etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Escribe un nombre o alias", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.setNombre(nombre);
            actualizarVista();
            Toast.makeText(this, "Nombre guardado", Toast.LENGTH_SHORT).show();
        });
        b.btnCatalogo.setOnClickListener(v -> startActivity(new Intent(this, CatalogActivity.class)));
        b.btnFavoritos.setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarVista();
    }

    private void actualizarVista() {
        String nombre = prefs.getNombre();
        b.tvBienvenida.setText(nombre.isEmpty() ? "¡Bienvenido!" : "¡Hola, " + nombre + "!");
        String seccion = prefs.getUltimaSeccion();
        b.tvResumen.setText("Preferencias guardadas\n"
                + "Nombre: " + (nombre.isEmpty() ? "(sin definir)" : nombre) + "\n"
                + "Tema: " + (prefs.isTemaOscuro() ? "Oscuro" : "Claro") + "\n"
                + "Última sección: " + (seccion.isEmpty() ? "(ninguna)" : seccion));
    }
}
