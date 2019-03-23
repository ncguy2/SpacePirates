package net.game.spacepirates.util.buffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.Disposable;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL30.glBindBufferBase;

public abstract class GLBuffer implements Disposable {

    protected int bufferId;
    protected transient int currentLocation;

    public abstract void init();
    public abstract int getType();

    public void bind() {
        Gdx.gl30.glBindBuffer(getType(), bufferId);
    }

    public void bind(int location) {
        bind();
        glBindBufferBase(getType(), location, bufferId);
        currentLocation = location;
    }

    public void unbind() {
        currentLocation = 0;
        Gdx.gl30.glBindBuffer(getType(), 0);
    }

    public ByteBuffer map(int access) {
        bind();
        return GL15.glMapBuffer(getType(), access);
    }

    public void unmap() {
        GL15.glUnmapBuffer(getType());
        unbind();
    }

    @Override
    public void dispose() {
        Gdx.gl.glDeleteBuffer(bufferId);
        bufferId = 0;
    }

    public void setData(ByteBuffer data) {
        data.position(0);
        Gdx.gl30.glBindBuffer(getType(), bufferId);
        Gdx.gl30.glBufferData(getType(), data.capacity(), data, GL30.GL_DYNAMIC_COPY);
        Gdx.gl30.glBindBuffer(getType(), 0);
    }

    public void using(Consumer<Integer> task) {
        bind();
        task.accept(bufferId);
        unbind();
    }

}
