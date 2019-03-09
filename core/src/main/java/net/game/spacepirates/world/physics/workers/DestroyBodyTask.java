package net.game.spacepirates.world.physics.workers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import net.game.spacepirates.world.physics.PhysicsService;

public class DestroyBodyTask extends PhysicsTask.VoidPhysicsTask {

    protected final Body body;

    public DestroyBodyTask(World world, Body body) {
        super(world);
        this.body = body;
    }

    public DestroyBodyTask(World world, PhysicsService service, Body body) {
        super(world, service);
        this.body = body;
    }

    @Override
    public void task() {
        service.queueRemoveBody(body);
    }

}
