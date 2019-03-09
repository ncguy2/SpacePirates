package net.game.spacepirates.world.physics.workers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import net.game.spacepirates.world.physics.PhysicsService;

public class CreateBodyTask extends PhysicsTask<Body> {

    protected final BodyDef def;

    public CreateBodyTask(World world, BodyDef def) {
        super(world);
        this.def = def;
    }

    public CreateBodyTask(World world, PhysicsService service, BodyDef def) {
        super(world, service);
        this.def = def;
    }

    @Override
    public Body run() {
        int i = service.queueCreateBody(def);
        Body body;
        while ((body = service.obtainBody(i)) == null) sleep(1);
        return body;
    }

}
