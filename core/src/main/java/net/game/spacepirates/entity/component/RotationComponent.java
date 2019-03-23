package net.game.spacepirates.entity.component;

public class RotationComponent extends SceneComponent<RotationComponent> {

    public float degreesPerSecond;

    public RotationComponent(String name) {
        super(name);
    }

    @Override
    public void update(float delta) {
        transform.rotate(degreesPerSecond * delta);
        super.update(delta);
    }
}
