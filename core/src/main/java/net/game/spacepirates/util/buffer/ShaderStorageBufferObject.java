package net.game.spacepirates.util.buffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import org.lwjgl.opengl.GL15;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class ShaderStorageBufferObject extends GLBuffer {

    private Buffer data;
    public final int size;

    public ShaderStorageBufferObject(Buffer data) {
        this(data, data.capacity());
    }

    public ShaderStorageBufferObject(int size) {
        this(null, size);
    }

    public ShaderStorageBufferObject(Buffer data, int size) {
        this.data = data;
        this.size = size;
    }

    @Override
    public void init() {
        if(data != null) {
            data.position(0);
        }else{
            data = ByteBuffer.allocateDirect(size);
        }

        bufferId = Gdx.gl.glGenBuffer();
        bind();
        Gdx.gl30.glBufferData(GL_SHADER_STORAGE_BUFFER, size, data, GL30.GL_DYNAMIC_COPY);
        unbind();
    }

    @Override
    public int getType() {
        return GL_SHADER_STORAGE_BUFFER;
    }

    public ByteBuffer getData() {
        ByteBuffer map = map(GL15.GL_READ_ONLY);
        unmap();
        return map;
    }
}
