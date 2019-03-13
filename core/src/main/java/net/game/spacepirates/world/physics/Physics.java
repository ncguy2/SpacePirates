package net.game.spacepirates.world.physics;

import net.game.spacepirates.world.PhysicsWorld;

public class Physics {

    public static float cm(float cm) {
        return cm * PhysicsWorld.SCREEN_TO_WORLD;
    }

    public static float m(float m) {
        return cm(m * 100);
    }

    public static float mm(float mm) {
        return cm(mm / 10);
    }

}
