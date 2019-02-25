package net.game.spacepirates.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class ComputeShader implements Disposable {

    protected FileHandle scriptHandle;
    protected String script;
    protected int programHandle;

    protected Map<String, Integer> uniformLocationCache;
    protected Map<String, String> macroParams;

    public ComputeShader(FileHandle scriptHandle) {
        this(scriptHandle, new HashMap<>());
    }

    public ComputeShader(FileHandle scriptHandle, Map<String, String> macroParams) {
        this.scriptHandle = scriptHandle;
        this.macroParams = macroParams;
        this.uniformLocationCache = new TreeMap<>();
        compile();
    }

    public int getUniformLocation(String uniform) {
        if(programHandle <= 0) {
            return -1;
        }

        if(uniformLocationCache.containsKey(uniform)) {
            return uniformLocationCache.get(uniform);
        }

        int loc = Gdx.gl.glGetUniformLocation(programHandle, uniform);
        if(loc > -1) {
            uniformLocationCache.put(uniform, loc);
        }
        return loc;
    }

    public void setUniform(String uniform, Consumer<Integer> setter) {
        int loc = getUniformLocation(uniform);
        if(loc > -1) {
            setter.accept(loc);
        }
    }

    public void bindSSBO(int bindingPoint, ShaderStorageBufferObject ssbo) {
        ssbo.bind(bindingPoint);
    }

    public void dispatch() {
        dispatch(1);
    }

    public void dispatch(int x) {
        dispatch(x, 1);
    }

    public void dispatch(int x, int y) {
        dispatch(x, y, 1);
    }

    public void dispatch(int x, int y, int z) {
        GL43.glDispatchCompute(x, y, z);
    }

    public void compile() {
        script = ShaderPreprocessor.ReadShader(scriptHandle, macroParams);
        compile(script);
    }

    public void compile(String script) {
        IntBuffer outBuffer = BufferUtils.newIntBuffer(8);
        int cs = Gdx.gl30.glCreateShader(GL43.GL_COMPUTE_SHADER);
        Gdx.gl30.glShaderSource(cs, script);
        Gdx.gl.glCompileShader(cs);
        Gdx.gl.glGetShaderiv(cs, GL20.GL_COMPILE_STATUS, outBuffer);
        if(outBuffer.get() != GL_TRUE) {
            System.err.println("Error compiling compute shader");
            String s = Gdx.gl.glGetShaderInfoLog(cs);
            System.err.println(s);
            return;
        }

        outBuffer.position(0);
        programHandle = Gdx.gl30.glCreateProgram();
        Gdx.gl.glAttachShader(programHandle, cs);
        Gdx.gl.glLinkProgram(programHandle);
        Gdx.gl.glGetProgramiv(programHandle, GL_LINK_STATUS, outBuffer);

        if(outBuffer.get() != GL_TRUE) {
            System.err.println("Error linking compute shader");
            String s = Gdx.gl.glGetProgramInfoLog(programHandle);
            System.err.println(s);
            return;
        }

        Gdx.gl.glDeleteShader(cs);
    }

    public void waitForCompletion() {
        GL42.glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
    }

    public void recompile() {
        dispose();
        compile();
    }

    public String getLog() {
        return Gdx.gl20.glGetProgramInfoLog(programHandle);
    }

    public void bind() {
        Gdx.gl.glUseProgram(programHandle);
    }

    public void unbind() {
        Gdx.gl.glUseProgram(0);
    }

    @Override
    public void dispose() {
        uniformLocationCache.clear();
        Gdx.gl.glUseProgram(0);
        Gdx.gl.glDeleteProgram(programHandle);
    }
}
