package net.game.spacepirates.engine;

import net.game.spacepirates.world.GameWorld;

public class WorldEngine extends Engine {

    public final GameWorld world;

    public WorldEngine(GameWorld world) {
        super();
        this.world = world;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        world.update(delta);
    }
}
