package net.game.spacepirates.entity.component;

public class TimedDeathComponent extends SceneComponent<TimedDeathComponent> {

    public float lifeRemaining;

    public TimedDeathComponent(String name) {
        super(name);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        lifeRemaining -= delta;

        if(lifeRemaining <= 0) {
            destroy();
        }

    }
}
