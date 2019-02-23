package net.game.spacepirates.world;

import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.RenderComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameWorld {

    public final List<Entity> entities;
    private final ExecutorService executorService;

    public GameWorld() {
        this.entities = new ArrayList<>();
        executorService = Executors.newWorkStealingPool();
    }

    public Entity addEntity() {
        Entity e = new Entity();
        entities.add(e);
        return e;
    }

    public boolean removeEntity(Entity e) {
        return entities.remove(e);
    }

    public synchronized void update(float delta) {
        List<Future<?>> futures = new ArrayList<>();
        for (Entity entity : entities) {
            Future<?> submit = executorService.submit(() -> entity.update(delta));
            futures.add(submit);
        }

        while(!futures.isEmpty()) {
            futures.removeIf(Future::isDone);
        }
    }

    public synchronized List<RenderComponent.RenderProxy> getRenderProxies() {
        List<RenderComponent.RenderProxy> proxies = new ArrayList<>();
        entities.stream()
                .filter(e -> e.has(RenderComponent.class))
                .map(e -> e.get(RenderComponent.class))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(r -> r.collectRenderables(proxies));
        return proxies;
    }

}
