package net.game.spacepirates.entity.types;

import com.badlogic.gdx.Gdx;
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
        particleComponent.systemName = "Fire";
        particleComponent.onInit = (comp, sys) -> {
            sys.addUniform("u_initialLife", loc -> {
                Gdx.gl.glUniform1f(loc, 10);
            });
            sys.addUniform("u_initialScale", loc -> {
                Gdx.gl.glUniform2f(loc, 4, 4);
            });
            sys.addUniform("u_simSpeed", loc -> {
                Gdx.gl.glUniform1f(loc, 1);
            });
            sys.addUniform("u_devianceRadius", loc -> {
                Gdx.gl.glUniform1f(loc, 24);
            });
        };
    }

    @Override
    public void assemble() {
        super.assemble();
        addComponent(inputComponent);
        addComponent(particleComponent);
    }
}
