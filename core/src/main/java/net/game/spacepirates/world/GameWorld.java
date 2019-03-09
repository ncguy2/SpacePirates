package net.game.spacepirates.world;

import com.badlogic.gdx.Gdx;
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
        e.rootComponent.components.forEach(c -> c.onRemoveFromEntity(e));
        e.rootComponent.components.clear();
        return entities.remove(e);
    }

    public synchronized void update(float delta) {
        List<Future<?>> futures = new ArrayList<>();
        for (Entity entity : entities) {
            if(!entity.isEnabled()) {
                continue;
            }
            Future<?> submit = executorService.submit(() -> entity.update(delta));
            futures.add(submit);
        }

        while(!futures.isEmpty()) {
            futures.removeIf(Future::isDone);
        }
    }

    public Stream<Entity> getEntitiesWith(Class<? extends EntityComponent>... components) {
        return this.entities.stream().filter(Entity::isEnabled).filter(e -> e.hasAll(components));
    }

    public Stream<RenderComponent> getRenderables() {
        List<RenderComponent> components = new ArrayList<>();
        entities.stream().filter(Entity::isEnabled).forEach(e -> components.addAll(e.getAll(RenderComponent.class)));
        return components.stream();
    }
}
