package net.game.spacepirates.entity.component;

import com.badlogic.gdx.Gdx;
import net.game.spacepirates.data.messaging.MessageBus;
import net.game.spacepirates.entity.Entity;

public abstract class EntityComponent<T extends EntityComponent> {

    public final String name;
    public Entity owningEntity;
    public SceneComponent parent;

    public EntityComponent(String name) {
        this.name = name;
    }

    public void update(float delta) {
    }

    public void onAddToParent(SceneComponent<?> parent) {
        MessageBus.get().dispatch(ComponentTopicRefs.COMPONENT_ATTACHED, this);
        this.parent = parent;
    }

    public void onRemoveFromParent() {
        MessageBus.get().dispatch(ComponentTopicRefs.COMPONENT_DETACHED, this);
        this.parent = null;
    }

    public void destroy() {
        Gdx.app.postRunnable(this::destroyImmediate);
    }

    public void destroyImmediate() {
        MessageBus.get().dispatch(ComponentTopicRefs.COMPONENT_DESTROYED, this);
        if(isRoot()) {
            owningEntity.destroy();
        }else{
            parent.removeComponent(this);
        }
    }

    public boolean isRoot() {
        return parent == null && owningEntity != null;
    }

    public void setRoot(Entity e) {
        owningEntity = e;
    }

    public static interface ComponentTopicRefs {

        String COMPONENT_ATTACHED = "world.entity.component.attached";
        String COMPONENT_DETACHED = "world.entity.component.detached";
        String COMPONENT_DESTROYED = "world.entity.component.destroyed";

    }

}
