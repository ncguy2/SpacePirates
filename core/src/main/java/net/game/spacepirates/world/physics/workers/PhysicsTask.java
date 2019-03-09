package net.game.spacepirates.world.physics.workers;

import com.badlogic.gdx.physics.box2d.World;
import net.game.spacepirates.services.Services;
import net.game.spacepirates.world.physics.PhysicsService;

public abstract class PhysicsTask<T> extends ThreadTask<T, PhysicsTask> {

    public transient World world;
    public transient PhysicsService service;

    public PhysicsTask(World world) {
        this(world, Services.get(PhysicsService.class));
    }

    public PhysicsTask(World world, PhysicsService service) {
        this.world = world;
        this.service = service;
    }

    public static abstract class VoidPhysicsTask extends PhysicsTask<Void> {

        public VoidPhysicsTask(World world) {
            super(world);
        }

        public VoidPhysicsTask(World world, PhysicsService service) {
            super(world, service);
        }

        public abstract void task();

        @Override
        public Void run() {
            task();
            return null;
        }
    }

}
