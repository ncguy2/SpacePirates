package net.game.spacepirates.asset;

import com.badlogic.gdx.files.FileHandle;
import net.game.spacepirates.util.io.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SPAssetSerialiser {

    public static <T extends SPAsset> T load(FileHandle source, Class<T> type) {
        return load(source.readString(), type);
    }

    public static <T extends SPAsset> T load(InputStream stream, Class<T> type) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String json = reader.lines().collect(Collectors.joining("\n"));
            return load(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends SPAsset> T load(String json, Class<T> type) {
        return Json.from(json, type);
    }

}
