package net.game.spacepirates.entity.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix3;
import net.game.spacepirates.entity.component.InputComponent;
import net.game.spacepirates.entity.component.ParticleComponent;

public class LocalPlayer extends Player {

    public InputComponent inputComponent;
    public ParticleComponent particleComponent;

    @Override
    public void init() {
        super.init();
        inputComponent = new InputComponent("Input");
        particleComponent = new ParticleComponent("Particle");
        particleComponent.systemName = "Fire";
        particleComponent.onInit = (comp, sys) -> {
            sys.bind("u_curve", comp.profile.curve);
            sys.addUniform("u_spawnMatrix", loc -> {
                Matrix3 matrix3 = transform.worldTransform();
                matrix3.setToTranslation(transform.translation);
                Gdx.gl.glUniformMatrix3fv(loc, 1, false, matrix3.val, 0);
            });
            sys.addUniform("u_initialScale", loc -> {
                Gdx.gl.glUniform2f(loc, 1, 1);
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
