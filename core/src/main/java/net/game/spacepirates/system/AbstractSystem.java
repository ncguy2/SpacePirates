package net.game.spacepirates.system;

import net.game.spacepirates.engine.Engine;
import net.game.spacepirates.world.GameWorld;

public abstract class AbstractSystem {

    protected final GameWorld operatingWorld;
    protected transient Engine attachedEngine;

    protected boolean bIsActive;

    public AbstractSystem(GameWorld operatingWorld) {
        this.operatingWorld = operatingWorld;
    }

    public void attachTo(Engine attachedEngine) {
        this.attachedEngine = attachedEngine;
    }

    public void detach() {
        this.attachedEngine = null;
    }

    public boolean isAttached() {
        return this.attachedEngine != null;
    }

    public abstract void startup();
    public abstract void update(float delta);
    public abstract void shutdown();

    public void setIsActive(boolean bIsActive) {
        this.bIsActive = bIsActive;
    }

    public boolean isActive() {
        return bIsActive;
    }
}
