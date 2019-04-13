package net.game.spacepirates.entity.component;

import com.badlogic.gdx.files.FileHandle;
import net.game.spacepirates.script.ScriptingEngine;
import net.game.spacepirates.util.TaskUtils;

public class ScriptComponent extends EntityComponent<ScriptComponent> {

    public FileHandle scriptSource;
    protected long sourceLastModified;
    protected String script;

    public ScriptComponent(String name) {
        super(name);
        invalidateScript();
    }

    public void invalidateScript() {
        TaskUtils.safeRun(() -> {
            script = scriptSource.readString();
            sourceLastModified = scriptSource.lastModified();
        });
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(scriptSource.lastModified() != sourceLastModified) {
            invalidateScript();
        }
        ScriptingEngine.get().eval(this, script);
    }
}
