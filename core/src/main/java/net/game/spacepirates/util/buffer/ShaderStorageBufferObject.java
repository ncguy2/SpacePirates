package net.game.spacepirates.util.buffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.Disposable;
import org.lwjgl.opengl.GL15;

import java.nio.*;

import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class ShaderStorageBufferObject implements Disposable {

    private final Buffer data;
    public final int size;

    protected int bufferId;

    public ShaderStorageBufferObject(Buffer data) {
        this(data, 0);
    }

    public ShaderStorageBufferObject(Buffer data, int location) {
        this(data, data.capacity(), location);
    }

    public ShaderStorageBufferObject(Buffer data, int size, int location) {
        this.data = data;
        this.size = size;
        init(location);
    }

    public ShaderStorageBufferObject(int size) {
        this(null, size, 0);
    }

    public void init(int location) {
        if(data != null) {
            data.position(0);
        }

        bufferId = Gdx.gl.glGenBuffer();
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        Gdx.gl30.glBufferData(GL_SHADER_STORAGE_BUFFER, size, data, GL30.GL_DYNAMIC_COPY);
        Gdx.gl30.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, location, bufferId);

        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0); // Unbind
    }

    public void bind(int location) {
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        Gdx.gl30.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, location, bufferId);
    }

    public void unbind() {
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public ByteBuffer map(int access) {
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId);
        return GL15.glMapBuffer(GL_SHADER_STORAGE_BUFFER, access);
    }

    public void unmap() {
        GL15.glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
        Gdx.gl30.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public ByteBuffer getData() {
        ByteBuffer map = map(GL15.GL_READ_ONLY);
        unmap();
        return map;
    }

    @Override
    public void dispose() {
        Gdx.gl.glDeleteBuffer(bufferId);
        bufferId = 0;
    }
}
