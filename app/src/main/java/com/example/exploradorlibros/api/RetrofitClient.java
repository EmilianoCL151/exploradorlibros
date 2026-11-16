package com.example.exploradorlibros.api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://ejercicio-08-documentacion-y-endpoints.onrender.com/";
    private static LibroApi api;

    public static synchronized LibroApi getApi() {
        if (api == null) {
            // Timeouts largos: Render (plan gratuito) tarda en "despertar" el servidor.
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            api = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(LibroApi.class);
        }
        return api;
    }
}
