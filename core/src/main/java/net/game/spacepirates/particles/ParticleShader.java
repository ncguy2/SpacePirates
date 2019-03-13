package net.game.spacepirates.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import net.game.spacepirates.services.Services;
import net.game.spacepirates.util.ComputeShader;
import net.game.spacepirates.util.ReloadableComputeShader;
import net.game.spacepirates.util.ShaderPreprocessor;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ParticleShader extends ReloadableComputeShader {

    protected int location;
    protected transient ShaderStorageBufferObject particleBuffer;

    protected List<ParticleBlock> blocks;

    public static Map<ParticleBlock.Type, ParticleShader> fromProfile(ParticleProfile profile, boolean register) {
        HashMap<ParticleBlock.Type, ParticleShader> map = new HashMap<>();

        FileHandle handle = Gdx.files.internal("particles/compute/framework.comp");
        map.put(ParticleBlock.Type.Spawn, new ParticleShader(profile.name + ": Spawn", handle, new HashMap<>(), register));
        map.put(ParticleBlock.Type.Update, new ParticleShader(profile.name + ": Update", handle, new HashMap<>(), register));

        for (String block : profile.blocks)
            Services.get(ParticleService.class)
                    .getParticleBlock(block)
                    .ifPresent(b -> {
                        ParticleShader ps = map.get(b.type);
                        if(ps != null)
                            ps.addBlock(b);
                    });

        if(register)
            map.values().forEach(ParticleShader::reloadImmediate);

        return map;
    }

    public ParticleShader(String name, FileHandle handle) {
        this(name, handle, new HashMap<>());
    }

    public ParticleShader(String name, FileHandle handle, Map<String, String> macroParams) {
        this(name, handle, macroParams, true);
    }
    public ParticleShader(String name, FileHandle handle, Map<String, String> macroParams, boolean register) {
        super(name, handle, macroParams, register);
    }

    public void addBlock(ParticleBlock block) {
        getBlocks().add(block);
    }

    public List<ParticleBlock> getBlocks() {
        if (blocks == null)
            blocks = new ArrayList<>();
        return blocks;
    }

    int getTypeScore(String type) {

        if(type.equalsIgnoreCase("float"))
            return 1;
        if(type.equalsIgnoreCase("vec2"))
            return 2;
        if(type.equalsIgnoreCase("vec3"))
            return 3;
        if(type.equalsIgnoreCase("vec4"))
            return 4;

        return 0;
    }

    boolean hasDefault(String u) {
        return u.contains("=");
    }

    public String getUniforms() {
        List<String> allUniforms = new ArrayList<>();
        for (ParticleBlock block : getBlocks()) {
            Collections.addAll(allUniforms, block.uniforms);
        }

        Map<String, List<String>> collect = allUniforms.stream()
                .collect(Collectors.groupingBy(s -> s.split(" ")[1]));

        allUniforms.clear();

        collect.forEach((name, entries) -> {
            if(entries.size() == 1) {
                allUniforms.add(entries.get(0));
                return;
            }

            System.out.println("Duplicate uniform detected: ");
            for (String entry : entries)
                System.out.println("\t" + entry);

            entries.sort((a, b) -> {
                String aType = a.split(" ")[0];
                String bType = b.split(" ")[0];
                int compare = Integer.compare(getTypeScore(aType), getTypeScore(bType));
                if(compare == 0)
                    return Boolean.compare(hasDefault(a), hasDefault(b));
                return compare;
            });

            String bestFit = entries.get(entries.size() - 1);
            System.out.println();
            System.out.println("\tUsing " + bestFit);
            allUniforms.add(bestFit);
        });

        String join = String.join("\n", allUniforms.stream().map(s -> {
            if(s.endsWith(";"))
                return s;
            return s + ";";
        }).map(s -> "uniform " + s).collect(Collectors.toList()));
        return join;
    }

    public String getDeclarations() {
        StringBuilder sb = new StringBuilder();
        for (ParticleBlock block : getBlocks())
            sb.append(block.methodSignature())
                    .append(";\n");
        return sb.toString();
    }

    public String getInvocations() {
        StringBuilder sb = new StringBuilder();
        for (ParticleBlock block : getBlocks())
            sb.append(block.methodName())
                    .append("(")
                    .append(block.datumKey())
                    .append(");\n");
        return sb.toString();
    }

    public String getDefinitions() {
        StringBuilder sb = new StringBuilder();
        for (ParticleBlock block : getBlocks()) {
            sb.append(block.methodSignature())
                    .append(" {\n")
                    .append(block.fragment("\t"))
                    .append("\n}\n");
        }
        return sb.toString();
    }

    public String getScript() {
        ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("uniforms", ParticleShader.this::getUniforms));
        ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("declarations", ParticleShader.this::getDeclarations));
        ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("invocations", ParticleShader.this::getInvocations));
        ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("definitions", ParticleShader.this::getDefinitions));
        return ShaderPreprocessor.ReadShader(handle, macroParams);
    }

    @Override
    public ComputeShader create() {
        return new ComputeShader(handle, macroParams) {
            @Override
            public void compile() {
                ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("uniforms", ParticleShader.this::getUniforms));
                ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("declarations", ParticleShader.this::getDeclarations));
                ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("invocations", ParticleShader.this::getInvocations));
                ShaderPreprocessor.SetBlock(new ShaderPreprocessor.ShaderInjectionBlock("definitions", ParticleShader.this::getDefinitions));
                super.compile();
            }
        };
    }

    public void setParticleBuffer(int location, ShaderStorageBufferObject buffer) {
        this.location = location;
        this.particleBuffer = buffer;
    }

    public void dispatch(){
        dispatch(1);
    }

    public void dispatch(int x) {
        dispatch(x, 1);
    }

    public void dispatch(int x, int y) {
        dispatch(x, y, 1);
    }

    public void dispatch(int x, int y, int z) {
        if (program != null)
            program.dispatch(x, y, z);
    }

    public void setUniform(String uniform, Consumer<Integer> setter) {
        if (program != null)
            program.setUniform(uniform, setter);
    }

}
