package net.game.spacepirates.util;

import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

public class ReloadableComputeShader extends ReloadableShader<ComputeShader> {

    protected final FileHandle handle;
    protected final Map<String, String> macroParams;

    public ReloadableComputeShader(String name, FileHandle handle) {
        this(name, handle, new HashMap<>());
    }

    public ReloadableComputeShader(String name, FileHandle handle, Map<String, String> macroParams) {
        this(name, handle, macroParams, true);
    }
    public ReloadableComputeShader(String name, FileHandle handle, Map<String, String> macroParams, boolean register) {
        super(name, register);
        this.handle = handle;
        this.macroParams = macroParams;
        if(register) {
            program = create();
        }
    }

    @Override
    public ComputeShader create() {
        return new ComputeShader(handle, macroParams);
    }

    @Override
    public void reloadImmediate() {
        if(program != null)
            program.recompile();
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
}
