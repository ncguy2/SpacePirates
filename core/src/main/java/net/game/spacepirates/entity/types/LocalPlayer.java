package net.game.spacepirates.entity.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import net.game.spacepirates.entity.component.CollisionComponent;
import net.game.spacepirates.entity.component.InputComponent;
import net.game.spacepirates.entity.component.ParticleComponent;
import net.game.spacepirates.entity.component.SceneComponent;

public class LocalPlayer extends Player {

    public InputComponent inputComponent;
    public ParticleComponent particleComponent;

    @Override
    public SceneComponent<?> defaultRootComponent() {
        return new CollisionComponent("Collision");
    }

    @Override
    public void init() {
        super.init();
        inputComponent = new InputComponent("Input");
        particleComponent = new ParticleComponent("Particle");
        particleComponent.systemName = "Fire 2";
        particleComponent.onInit = (comp, sys) -> {
            sys.addUniform("u_initialLife", loc -> {
                Gdx.gl.glUniform1f(loc, 5);
            });
            sys.addUniform("u_initialScale", loc -> {
                Gdx.gl.glUniform2f(loc, 1, 1);
            });
            sys.addUniform("u_simSpeed", loc -> {
                Gdx.gl.glUniform1f(loc, 1);
            });
            sys.addUniform("u_circleRadius", loc -> {
                Gdx.gl.glUniform1f(loc, 32);
            });
            sys.addUniform("u_devianceRadius", loc -> {
                Gdx.gl.glUniform1f(loc, 0);
            });
            sys.addUniform("u_speedScale", loc -> {
                Gdx.gl.glUniform1f(loc, 64);
            });
            sys.addUniform("u_origin", loc -> {
                Vector2 pos = particleComponent.transform.worldTranslation();
                Gdx.gl.glUniform2f(loc, pos.x, pos.y);
            });
            sys.addUniform("u_vectorFieldIntensity", loc -> {
                Gdx.gl.glUniform1f(loc, 32);
            });
        };
    }

    @Override
    public void assemble() {
        super.assemble();
        addComponent(inputComponent);
        addComponent(particleComponent);
    }

    @Override
    public void initTransform() {
        super.initTransform();
        particleComponent.transform.translation.set(32, 32);
    }
}
