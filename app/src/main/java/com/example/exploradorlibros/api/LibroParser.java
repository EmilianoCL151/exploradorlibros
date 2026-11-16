package com.example.exploradorlibros.api;

import com.example.exploradorlibros.models.Libro;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Extrae la lista de libros sea un arreglo plano, {"_embedded":{...:[...]}} o {"content":[...]}. */
public class LibroParser {
    public static List<Libro> parse(JsonElement raiz) {
        JsonArray arr = buscarArreglo(raiz);
        if (arr == null) return new ArrayList<>();
        Type tipo = new TypeToken<List<Libro>>() {}.getType();
        List<Libro> lista = new Gson().fromJson(arr, tipo);
        return lista != null ? lista : new ArrayList<>();
    }

    private static JsonArray buscarArreglo(JsonElement e) {
        if (e == null || e.isJsonNull()) return null;
        if (e.isJsonArray()) return e.getAsJsonArray();
        if (e.isJsonObject()) {
            JsonObject o = e.getAsJsonObject();
            for (Map.Entry<String, JsonElement> en : o.entrySet()) {
                if (en.getKey().equals("_links")) continue;
                JsonArray r = buscarArreglo(en.getValue());
                if (r != null) return r;
            }
        }
        return null;
    }
}
