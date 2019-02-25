package net.game.spacepirates.util;

import com.badlogic.gdx.Gdx;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class ReloadableShader<T> {

    protected final String name;
    protected T program;

    public static List<WeakReference<ReloadableShader<?>>> shaders = new ArrayList<>();

    public ReloadableShader(String name) {
        this(name, true);
    }
    public ReloadableShader(String name, boolean register) {
        if(register)
            register();
        this.name = name;
    }

    public abstract T create();

    private void register() {
        shaders.add(new WeakReference<>(this));
    }

    public void reload() {
        Gdx.app.postRunnable(this::reloadImmediate);
    }

    public abstract void reloadImmediate();

    public T program() {
        return program;
    }

    public abstract String getLog();

    public String name() {
        return name;
    }

    public abstract void shutdown();
}
