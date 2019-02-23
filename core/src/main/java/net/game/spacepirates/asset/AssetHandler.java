package net.game.spacepirates.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AssetHandler implements Disposable {

    private static AssetHandler instance;

    public boolean generateMipmaps = true;
    protected Map<String, Consumer<?>> asyncRequests;
    protected AssetManager manager;
    boolean isLoading = false;

    private AssetHandler() {
        manager = new AssetManager();
        asyncRequests = new HashMap<>();

        manager.setErrorListener((asset, throwable) -> {
            throwable.printStackTrace();
            asyncRequests.remove(asset.fileName);
        });

    }

    public static void Start() {
        instance();
    }

    public static void Dispose() {
        WithInstanceIfExists(AssetHandler::dispose);
    }

    public static AssetHandler instance() {
        if (instance == null)
            instance = new AssetHandler();
        return instance;
    }

    public static void WithInstanceIfExists(Consumer<AssetHandler> task) {
        if (instance != null)
            task.accept(instance);
    }

    public boolean IsLoading() {
        return this.isLoading;
    }

    public float GetProgress() {
        return manager.getProgress();
    }

    public void Update() {
        isLoading = !manager.update();
        asyncRequests.entrySet()
                .stream()
                .filter(e -> manager.isLoaded(e.getKey()))
                .peek(e -> e.getValue()
                        .accept(manager.get(e.getKey())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(asyncRequests::remove);
    }

    public <T> T Get(String pth, Class<T> type) {

        if (pth == null || pth.isEmpty())
            return null;

        final String path = pth;

        if (!manager.isLoaded(path, type)) {

            FileHandle handle = Gdx.files.internal(path);

            if (!handle.exists())
                handle = Gdx.files.external(path);

            if (!handle.exists())
                return null;

            AtomicReference<T> item = new AtomicReference<>();

            FileHandle finalHandle = handle;
            AssetDescriptor tAssetDescriptor;
            if (type.equals(Texture.class)) {
                TextureLoader.TextureParameter p = new TextureLoader.TextureParameter();
                p.genMipMaps = generateMipmaps;
                p.minFilter = Texture.TextureFilter.MipMapLinearLinear;
                p.magFilter = Texture.TextureFilter.Linear;
                p.wrapU = Texture.TextureWrap.Repeat;
                p.wrapV = Texture.TextureWrap.Repeat;
                tAssetDescriptor = new AssetDescriptor<>(finalHandle, Texture.class, p);
            } else tAssetDescriptor = new AssetDescriptor<>(finalHandle, type);
            manager.load(tAssetDescriptor);
            manager.finishLoadingAsset(path);
            item.set(manager.get(path, type));

            return item.get();
        }

        return manager.get(path, type);
    }

    public <T> void GetAsync(String path, Class<T> type, Consumer<T> func) {
        if (path == null || path.isEmpty())
            return;

        if (manager.isLoaded(path, type)) {
            func.accept(manager.get(path, type));
            return;
        }

        FileHandle handle = Gdx.files.internal(path);

        if (handle.exists() && handle.isDirectory()) {
            return;
        }


        if (!handle.exists())
            handle = Gdx.files.external(path);

        if (!handle.exists() || handle.isDirectory() || handle.extension().isEmpty())
            return;

        path = handle.path();

        if (manager.isLoaded(path, type)) {
            func.accept(manager.get(path, type));
            return;
        }

        if (asyncRequests.containsKey(path)) return;
        if (IsAbsolutePath(path)) {
            if (handle.exists() && !handle.isDirectory())
                manager.load(new AssetDescriptor<>(handle, type));
        } else {
            if (generateMipmaps && type.equals(Texture.class)) {
                TextureLoader.TextureParameter p = new TextureLoader.TextureParameter();
                p.genMipMaps = true;
                p.minFilter = Texture.TextureFilter.MipMapLinearLinear;
                p.magFilter = Texture.TextureFilter.Linear;
                p.wrapU = Texture.TextureWrap.Repeat;
                p.wrapV = Texture.TextureWrap.Repeat;
                manager.load(path, Texture.class, p);
            } else manager.load(path, type);
        }
    }

    public <T> List<T> GetOfType(Class<T> type) {
        Array<T> objects = new Array<>();
        manager.getAll(type, objects);
        List<T> list = new ArrayList<>();
        objects.forEach(list::add);
        return list;
    }

    public boolean IsAbsolutePath(String path) {
        if (path.length() >= 2)
            return path.charAt(1) == ':';
        return false;
    }

    public boolean IsLoaded(String path) {
        return manager.isLoaded(path);
    }

    public boolean IsLoaded(String path, Class<?> cls) {
        return manager.isLoaded(path, cls);
    }

    public void UsingManager(Consumer<AssetManager> func) {
        func.accept(manager);
    }

    public <T> String GetAssetFileName(T asset) {
        return manager.getAssetFileName(asset);
    }

    public <T> List<T> AllAssetsOfTypeInRegistry(Class<T> type) {
        List<T> list = new ArrayList<>();
        AllAssetsOfTypeInRegistry(type, list);
        return list;
    }

    public <T> void AllAssetsOfTypeInRegistry(Class<T> type, List<T> list) {
        Array<T> out = new Array<>();
        manager.getAll(type, out);
        out.forEach(list::add);
    }

    @Override
    public void dispose() {
        manager.dispose();
        instance = null;
    }

}