package net.game.spacepirates.util.buffer;

import com.badlogic.gdx.Gdx;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.GL_ATOMIC_COUNTER_BUFFER;

public class AtomicBuffer extends GLBuffer {

    @Override
    public void init() {
        bufferId = Gdx.gl.glGenBuffer();

        using(id -> glBufferData(getType(), Integer.BYTES, GL_DYNAMIC_DRAW));
        reset();
    }

    public void reset() {
        set(0);
    }

    public int getAndReset() {
        ByteBuffer map = map(GL_READ_WRITE);
        int val = map.getInt(0);
        map.putInt(0, 0);
        unmap();
        return val;
    }

    public int get() {
        ByteBuffer map = map(GL_READ_ONLY);
        int val = map.getInt(0);
        unmap();
        return val;
    }

    public void set(int value) {
        ByteBuffer map = map(GL_WRITE_ONLY);
        map.putInt(0, value);
        unmap();
    }

    @Override
    public int getType() {
        return GL_ATOMIC_COUNTER_BUFFER;
    }
}
