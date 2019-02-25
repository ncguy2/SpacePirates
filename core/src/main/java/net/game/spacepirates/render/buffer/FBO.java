package net.game.spacepirates.render.buffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FBO implements Disposable, IStackableFBO {

    private final Builder builder;
    private FrameBuffer buffer;
    private String name = UUID.randomUUID().toString();

    public FBO(Builder builder) {
        this.builder = builder;
        build();
    }

    public FBO(Pixmap.Format format, int width, int height, boolean hasDepth) {
        this(format, width, height, hasDepth, false);
    }

    public FBO(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
        builder = new Builder(width, height);
        builder.addBasicColorTextureAttachment(format);
        if(hasDepth) {
            builder.addBasicDepthRenderBuffer();
        }
        if(hasStencil) {
            builder.addBasicStencilRenderBuffer();
        }
        build();
    }

    public void build() {
        resize(builder.width(), builder.height());
    }

    public Optional<FrameBuffer> buffer() {
        return Optional.ofNullable(buffer);
    }

    public FBO name(String newName) {
        this.name = newName;
        return this;
    }

    public Optional<Texture[]> getTextures() {
        return buffer().map(FrameBuffer::getTextureAttachments).map(o -> o.toArray(Texture.class));
    }

    public List<Texture> getTextureAttachments() {
        List<Texture> texArr = new ArrayList<>();
        getTextures().ifPresent(texs -> {
            for (Texture tex : texs) {
                if(tex != null) {
                    texArr.add(tex);
                }
            }
        });
        return texArr;
    }

    public Texture getColorBufferTexture() {
        return buffer.getColorBufferTexture();
    }

    public int width() {
        return builder.width();
    }

    public int height() {
        return builder.height();
    }

    public FBO resize(int width, int height) {
        if(width <= 0 || height <= 0) {
            return this;
        }

        if(buffer != null) {
            if(width == buffer.getWidth() && height == buffer.getHeight()) {
                return this;
            }

            buffer.dispose();
            buffer = null;
        }

        builder.resize(width, height);
        buffer = builder.build();
        return this;
    }

    public void begin() {
        FBOStack.push(this);
    }

    public void end() {
        FBOStack.pop();
    }

    public int getDepthBufferHandle() {
        return buffer.getDepthBufferHandle();
    }

    public void clear(Color colour, boolean clearDepth, boolean clearStencil) {
        Gdx.gl.glClearColor(colour.r, colour.g, colour.b, colour.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | (clearDepth ? GL20.GL_DEPTH_BUFFER_BIT : 0) | (clearStencil ? GL20.GL_STENCIL_BUFFER_BIT : 0));
    }

    @Override
    public void dispose() {
        if(buffer != null) {
            buffer.dispose();
            buffer = null;
        }
    }

    @Override
    public void beginFBO() {
        buffer().ifPresent(GLFrameBuffer::begin);
    }

    @Override
    public void endFBO() {
        buffer().ifPresent(GLFrameBuffer::end);
    }

    @Override
    public String name() {
        return name;
    }

    public static class Builder extends GLFrameBuffer.FrameBufferBuilder {

        public Builder(int width, int height) {
            super(width, height);
        }

        public void resize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }

        public FBO buildFbo() {
            return new FBO(this);
        }

    }

}
