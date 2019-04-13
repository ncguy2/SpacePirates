package net.game.spacepirates.script;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.Objects;

public class ScriptFunction {

    private final ScriptObjectMirror mirrorObject;

    /**
     * @param mirrorObject Invariant: Must always be a valid function object mirror at time of creation
     */
    public ScriptFunction(ScriptObjectMirror mirrorObject) {
        if(!Objects.requireNonNull(mirrorObject).isFunction()) {
            throw new IllegalArgumentException("mirrorObject is not a function");
        }
        this.mirrorObject = mirrorObject;
    }

    public void call(Object context, Object... parameters) {
        mirrorObject.call(context, parameters);
    }

}
