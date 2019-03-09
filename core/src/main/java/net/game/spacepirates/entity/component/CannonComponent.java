package net.game.spacepirates.entity.component;

import com.badlogic.gdx.math.Vector2;
import net.game.spacepirates.entity.types.Projectile;

public class CannonComponent extends SceneComponent<CannonComponent> {

    public boolean hasFired = false;

    public CannonComponent(String name) {
        super(name);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

    }

    public void fire() {
        parentEntity.world.addEntity(Projectile.class, e -> {
            Vector2 v = this.transform.worldTranslation();
            e.getTransform().translation.set(v);
            e.getTransform().rotation = 180;
        });
        hasFired = true;
    }

}
