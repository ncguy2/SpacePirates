package net.game.spacepirates.script;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;

public class ScriptObject {

    public Object context;
    public String source;

    private ScriptFunction Function_OnUpdate;

    public ScriptObject(String source) {
        this.context = this;
        this.source = source;
    }

    public void Parse() throws ScriptException {
        ScriptingEngine scriptingEngine = ScriptingEngine.get();
        ScriptContext scriptContext = scriptingEngine.bindContext(context, engine -> engine.eval(this.source));
        readFromContext(scriptContext);
    }

    public void readFromContext(ScriptContext ctx) {
        Bindings bindings = ctx.getBindings(ScriptContext.ENGINE_SCOPE);
        Function_OnUpdate = ScriptingEngine.LoadFunctionMirror(bindings, "OnUpdate");
    }

    public void onUpdate(float delta) {
        call(Function_OnUpdate, delta);
    }

    @SuppressWarnings("WeakerAccess")
    protected void call(ScriptFunction function, Object... arguments) {
        if(function == null) {
            return;
        }
        function.call(this, arguments);
    }

}
