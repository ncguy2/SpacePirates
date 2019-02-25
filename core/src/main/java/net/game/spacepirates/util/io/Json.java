package net.game.spacepirates.util.io;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class Json {

    private static Json instance;
    public static Json instance() {
        if (instance == null)
            instance = new Json();
        return instance;
    }

    Gson gson;
    GsonBuilder builder;

    private Json() {
        builder = new GsonBuilder();

        builder.setPrettyPrinting();
        builder.serializeNulls();
        builder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
        builder.registerTypeAdapter(FileHandle.class, new FileHandleTypeAdapter());
        builder.registerTypeAdapter(Color.class, new ColourTypeAdapter());

        _invalidateGson();
//        RuntimeTypeAdapterFactory<EntityComponent> entityAdapter = RuntimeTypeAdapterFactory.of(EntityComponent.class)
//        builder.registerTypeAdapter()
    }

    public static void WithBuilder(Consumer<GsonBuilder> task) {
        task.accept(instance().builder);
        instance()._invalidateGson();
    }

    protected void _register(TypeAdapterFactory factory) {
        builder.registerTypeAdapterFactory(factory);
    }

    protected void _invalidateGson() {
        this.gson = builder.create();
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static void register(TypeAdapterFactory... factories) {
        for (TypeAdapterFactory factory : factories)
            instance()._register(factory);
        instance()._invalidateGson();
    }

    public static String to(Object obj) {
        return instance().toJson(obj);
    }

    public static <T> T from(String json, Class<T> type) {
        return instance().fromJson(json, type);
    }

    public static <T> T from(File file, Class<T> type) {
        try {
            String json = String.join("\n", Files.readAllLines(file.toPath()));
            return from(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
