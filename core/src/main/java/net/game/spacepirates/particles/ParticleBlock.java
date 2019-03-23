package net.game.spacepirates.particles;

import java.util.UUID;

public class ParticleBlock {

    public transient String methodName;

    public String name;
    public Type type;
    public String datumKey;
    public String description;
    public String[] imports;
    public String[] uniforms;
    public String[] fragment;

    public String datumKey() {
        return datumKey;
    }

    public String[] getImports() {
        if(imports == null) {
            imports = new String[0];
        }
        return imports;
    }

    public String getUniforms() {
        StringBuilder sb = new StringBuilder();
        for (String uniform : uniforms) {
            sb.append(uniform).append(";\n");
        }
        return sb.toString();
    }

    public String methodSignature() {
        return "void " + methodName() + "(inout ParticleData " + datumKey() + ")";
    }

    public String methodName() {
        if (methodName == null) {
            methodName = name.replace(" ", "") +
                    "_" +
                    UUID.randomUUID()
                        .toString()
                        .replaceAll("-", "");
        }
        return methodName;
    }

    public String fragment() {
        return fragment("");
    }

    public String fragment(String linePrefix) {
        StringBuilder sb = new StringBuilder();
        for (String s : fragment) {
            sb.append(linePrefix).append(s).append("\n");
        }
        String s = sb.toString();

        if(s.isEmpty()) {
            return "// NOOP";
        }

        return s.substring(0, s.lastIndexOf('\n'));
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Type {
        Spawn,
        Update
    }

}
