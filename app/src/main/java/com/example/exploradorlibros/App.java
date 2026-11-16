package com.example.exploradorlibros;

import android.app.Application;
import com.example.exploradorlibros.ui.PreferencesManager;

/** Aplica el tema guardado en SharedPreferences antes de mostrar cualquier pantalla. */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesManager.aplicarTema(new PreferencesManager(this).isTemaOscuro());
    }
}
