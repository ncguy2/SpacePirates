package net.game.spacepirates.asset;

import com.badlogic.gdx.files.FileHandle;

import java.io.InputStream;

public class SPAssetSerialiser {

    public static <T extends SPAsset> T load(FileHandle source, Class<T> type) {
        return load(source.readString(), type);
    }

    public static <T extends SPAsset> T load(InputStream stream, Class<T> type) {
        return JSON.get().read(stream, type);
    }

    public static <T extends SPAsset> T load(String json, Class<T> type) {
        return JSON.get().read(json, type);
    }

}
