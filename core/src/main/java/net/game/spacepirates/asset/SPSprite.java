package net.game.spacepirates.asset;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class SPSprite extends SPAsset {

    public Vector2 size;
    public String texture;

    @Override
    public <T> T convert(Class<T> type) {

        if(type.equals(Sprite.class)) {
            Texture tex = AssetHandler.get().Get(texture, Texture.class);
            Sprite s = new Sprite(tex);
            s.setSize(size.x, size.y);
            return (T) s;
        }

        return super.convert(type);
    }

    public static SPSprite of(FileHandle handle) {
        return of(handle, SPSprite.class);
    }

}
