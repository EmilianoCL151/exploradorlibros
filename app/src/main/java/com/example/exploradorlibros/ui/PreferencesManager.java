package com.example.exploradorlibros.ui;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/** Solo guarda nombre, tema y última sección. Sin datos sensibles. */
public class PreferencesManager {
    public static final String SECCION_CATALOGO = "Catálogo";
    public static final String SECCION_FAVORITOS = "Favoritos";

    private static final String ARCHIVO = "preferencias_usuario";
    private static final String K_NOMBRE = "nombre";
    private static final String K_TEMA_OSCURO = "tema_oscuro";
    private static final String K_SECCION = "ultima_seccion";

    private final SharedPreferences sp;

    public PreferencesManager(Context ctx) {
        sp = ctx.getApplicationContext().getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    public String getNombre() { return sp.getString(K_NOMBRE, ""); }
    public void setNombre(String n) { sp.edit().putString(K_NOMBRE, n).apply(); }

    public boolean isTemaOscuro() { return sp.getBoolean(K_TEMA_OSCURO, false); }
    public void setTemaOscuro(boolean v) { sp.edit().putBoolean(K_TEMA_OSCURO, v).apply(); }

    public String getUltimaSeccion() { return sp.getString(K_SECCION, ""); }
    public void setUltimaSeccion(String s) { sp.edit().putString(K_SECCION, s).apply(); }

    public static void aplicarTema(boolean oscuro) {
        AppCompatDelegate.setDefaultNightMode(
                oscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
