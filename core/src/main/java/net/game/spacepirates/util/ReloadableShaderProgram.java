package net.game.spacepirates.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.HashMap;
import java.util.Map;

public class ReloadableShaderProgram extends ReloadableShader<ShaderProgram> {

    private final FileHandle vertexShader;
    private final FileHandle fragmentShader;

    protected final Map<String, String> macroParams;

    public ReloadableShaderProgram(String name, FileHandle vertexShader, FileHandle fragmentShader) {
        this(name, vertexShader, fragmentShader, new HashMap<>());
    }

    public ReloadableShaderProgram(String name, FileHandle vertexShader, FileHandle fragmentShader, Map<String, String> macroParams) {
        super(name);
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.macroParams = macroParams;
        reloadImmediate();
    }

    @Override
    public ShaderProgram create() {
        System.out.println("Creating shader: " + name());
        String vert = ShaderPreprocessor.ReadShader(this.vertexShader, macroParams);
        String frag = ShaderPreprocessor.ReadShader(this.fragmentShader, macroParams);
        ShaderProgram shaderProgram = new ShaderProgram(vert, frag);
        System.out.println(shaderProgram.getLog());
        return shaderProgram;
    }

    @Override
    public void reloadImmediate() {
        ShaderProgram program = create();
        System.out.println(program.getLog());
        if(program.isCompiled()) {
            if(this.program != null)
                this.program.dispose();
            this.program = program;
            return;
        }
        System.out.println(name + " could not compile");
    }

    @Override
    public String getLog() {
        if(program != null)
            return program.getLog();
        return "No program";
    }

    @Override
    public void shutdown() {
        if(program != null) {
            program.dispose();
            program = null;
        }
    }

    public void bindTexture(String loc, Texture texture, int id) {
        if(program != null) {
            texture.bind(id);
            program.setUniformi(loc, id);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        }
    }
}
