package net.game.spacepirates.asset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;

public class JSON {

    private static JSON instance;
    public static JSON get() {
        if(instance == null) {
            instance = new JSON();
        }
        return instance;
    }

    private final Gson gson;

    private JSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        gson = builder.create();
    }

    public String write(Object obj) {
        return gson.toJson(obj);
    }

    public <T> T read(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public <T> T read(InputStream stream, Class<T> type) {
        return gson.fromJson(new InputStreamReader(stream), type);
    }

}
