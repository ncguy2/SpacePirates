package net.game.spacepirates.script;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.game.spacepirates.util.TaskUtils;

import javax.script.*;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ScriptingEngine {

    public static final int USER_BINDING_SCOPE = ScriptContext.GLOBAL_SCOPE;
    private static ScriptingEngine instance;
    private Map<WeakReference<Object>, ScriptContext> contextMap;
    private ScriptEngineManager mgr;
    private ThreadLocal<ScriptEngine> nashornEngines;
    private SimpleScriptContext defaultContext;

    private ScriptingEngine() {
        mgr = new ScriptEngineManager();
        nashornEngines = ThreadLocal.withInitial(() -> mgr.getEngineByName("nashorn"));
        contextMap = new ConcurrentHashMap<>();
    }

    public static ScriptingEngine get() {
        if (instance == null) {
            instance = new ScriptingEngine();
        }
        return instance;
    }

    public static Optional<ScriptObjectMirror> LoadObjectMirror(Bindings bindings, String id, Predicate<ScriptObjectMirror> filter) {
        if (bindings.containsKey(id)) {
            Object o = bindings.get(id);
            if (o instanceof ScriptObjectMirror) {
                ScriptObjectMirror mirror = (ScriptObjectMirror) o;
                if (filter.test(mirror))
                    return Optional.of(mirror);
            }
        }
        return Optional.empty();
    }

    public static ScriptFunction LoadFunctionMirror(Bindings bindings, String id) {
        return LoadObjectMirror(bindings, id, ScriptObjectMirror::isFunction).map(ScriptFunction::new).orElse(null);
    }

    public static ScriptFunction LoadFunctionMirror(Object handle, String id) {
        return LoadObjectMirror(handle, id, ScriptObjectMirror::isFunction).map(ScriptFunction::new).orElse(null);
    }

    public static Optional<ScriptObjectMirror> LoadObjectMirror(Object handle, String id, Predicate<ScriptObjectMirror> filter) {
        Bindings bindings = get().getContextBindings(handle);
        return LoadObjectMirror(bindings, id, filter);
    }

    public ScriptContext getDefaultContext() {
        if (defaultContext == null) {
            defaultContext = new SimpleScriptContext();
            ScriptEngine engine = getEngine();
            defaultContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
            defaultContext.setBindings(engine.createBindings(), ScriptContext.GLOBAL_SCOPE);
        }
        return defaultContext;
    }

    public ScriptContext getContext(Object handle) {
        cullDeadContexts();
        for (Map.Entry<WeakReference<Object>, ScriptContext> entry : contextMap.entrySet()) {
            if (Objects.equals(handle, entry.getKey().get())) {
                return entry.getValue();
            }
        }

        return createContext(handle);
    }

    public Bindings getContextBindings(Object handle) {
        return getContext(handle).getBindings(USER_BINDING_SCOPE);
    }

    public void eval(Object handle, Reader source) {
        TaskUtils.safeRun(() -> bindContext(handle, engine -> engine.eval(source)));
    }

    public void eval(Object handle, String script) {
        TaskUtils.safeRun(() -> bindContext(handle, engine -> engine.eval(script)));
    }

    private ScriptEngine getEngine() {
        return nashornEngines.get();
    }

    public ScriptContext bindContext(Object handle, ScriptConsumer task) throws ScriptException {
        return bindContext(getContext(handle), task);
    }

    private ScriptContext bindContext(ScriptContext context, ScriptConsumer task) throws ScriptException {
        ScriptEngine engine = getEngine();
        engine.setContext(context);
        if (task != null) {
            task.accept(engine);
        }
        return context;
    }

    private ScriptContext createContext(Object handle) {
        ScriptContext defCtx = getDefaultContext();

        SimpleScriptContext value = new SimpleScriptContext();

        ScriptEngine engine = getEngine();

        Bindings engineBindings = defCtx.getBindings(ScriptContext.ENGINE_SCOPE);
        Bindings globalBindings = engine.createBindings();

        Bindings defaultGlobalBindings = defCtx.getBindings(ScriptContext.GLOBAL_SCOPE);
        globalBindings.putAll(defaultGlobalBindings);

        value.setBindings(engineBindings, ScriptContext.ENGINE_SCOPE);
        value.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);
        contextMap.put(new WeakReference<>(handle), value);
        return value;
    }

    private void cullDeadContexts() {
        contextMap.keySet()
                  .stream()
                  .filter(o -> o.get() == null)
                  .collect(Collectors.toList())
                  .forEach(contextMap::remove);
    }

    @FunctionalInterface
    public interface ScriptConsumer {
        void accept(ScriptEngine e) throws ScriptException;
    }

}
