package net.game.spacepirates.asset;


import com.badlogic.gdx.files.FileHandle;

public abstract class SPAsset {

    public String id;

    public <T> T convert(Class<T> type) {
        throw new UnsupportedOperationException("Cannot convert " + getClass().getSimpleName() + " to " + type.getSimpleName());
    }

    public static <T extends SPAsset> T of(FileHandle handle, Class<T> type) {
        return SPAssetSerialiser.load(handle, type);
    }

}
