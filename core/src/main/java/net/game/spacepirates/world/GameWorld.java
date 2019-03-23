package net.game.spacepirates.world;

import com.badlogic.gdx.Gdx;
import net.game.spacepirates.data.messaging.MessageBus;
import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.EntityComponent;
import net.game.spacepirates.entity.component.RenderComponent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class GameWorld {

    public final List<Entity> entities;
    private final ExecutorService executorService;

    public GameWorld() {
        this.entities = new ArrayList<>();
        executorService = Executors.newWorkStealingPool();
    }

    public void addEntity() {
        addEntity(Entity.class);
    }

    public void addEntity(Consumer<Entity> task) {
        Gdx.app.postRunnable(() -> task.accept(addEntityImmediate()));
    }

    public <T extends Entity> void addEntity(Class<T> type, Consumer<T> task) {
        Gdx.app.postRunnable(() -> task.accept(addEntityImmediate(type)));
    }

    public <T extends Entity> void addEntity(Class<T> type) {
        Gdx.app.postRunnable(() -> addEntityImmediate(type));
    }

    public Entity addEntityImmediate() {
        return addEntityImmediate(Entity.class);
    }

    public <T extends Entity> T addEntityImmediate(Class<T> entityType) {
        try {
            T entity = createEntity_Impl(entityType);
            entities.add(entity);
            return entity;
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private <T extends Entity> T createEntity_Impl(Class<T> entityType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T e = entityType.getConstructor().newInstance();
        e.setWorld(this);
        return e;
    }

    public void removeEntity(Entity e) {
        Gdx.app.postRunnable(() -> removeEntityImmediate(e));
    }
    public boolean removeEntityImmediate(Entity e) {
        e.rootComponent.components.forEach(EntityComponent::onRemoveFromParent);
        e.rootComponent.components.clear();
        e.rootComponent.onRemoveFromParent();
        MessageBus.get().dispatch(Entity.EntityTopics.ENTITY_DESTROYED, this);
        return entities.remove(e);
    }

    public synchronized void update(float delta) {
        List<Future<?>> futures = new ArrayList<>();
        for (Entity entity : entities) {
            if(!entity.isEnabled()) {
                continue;
            }
//            Future<?> submit = executorService.submit(new NamedRunnable(entity.rootComponent.name, () -> entity.update(delta)));
//            futures.add(submit);
            entity.update(delta);
        }

//        while(!futures.isEmpty()) {
//            futures.removeIf(Future::isDone);
//        }
    }

    public Stream<Entity> getEntitiesWith(Class<? extends EntityComponent>... components) {
        return this.entities.stream().filter(Entity::isEnabled).filter(e -> e.hasAll(components));
    }

    public Stream<RenderComponent> getRenderables() {
        List<RenderComponent> components = new ArrayList<>();
        entities.stream().filter(Entity::isEnabled).forEach(e -> components.addAll(e.getAll(RenderComponent.class)));
        return components.stream();
    }

    public static class NamedRunnable implements Runnable {

        private final String name;
        private final Runnable task;

        public NamedRunnable(String name, Runnable task) {
            this.name = name;
            this.task = task;
        }

        @Override
        public void run() {
            task.run();
        }
    }

}
