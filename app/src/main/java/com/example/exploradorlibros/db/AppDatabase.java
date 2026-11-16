package com.example.exploradorlibros.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LibroEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    /** Hilo secundario para todo acceso a Room (nunca se usa el hilo principal). */
    public static final ExecutorService IO = Executors.newSingleThreadExecutor();
    private static volatile AppDatabase instancia;

    public abstract LibroDao libroDao();

    public static AppDatabase getInstance(Context ctx) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(ctx.getApplicationContext(),
                            AppDatabase.class, "libros_db").build();
                }
            }
        }
        return instancia;
    }
}
