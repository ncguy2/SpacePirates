package net.game.spacepirates.render.buffer;

import com.badlogic.gdx.graphics.Pixmap;

public class FBOFactory {

    public static FBO buildDefaultBuffer(int width, int height) {
        FBO.Builder builder = new FBO.Builder(width, height);

        builder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888); // Diffuse
        builder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888); // Normal
        builder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888); // Emissive
        builder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888); // RG: Texture coordinates, B: Occlusion

        builder.addBasicDepthRenderBuffer();

        return builder.buildFbo();
    }

}
