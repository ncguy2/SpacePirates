package net.game.spacepirates.particles.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import net.game.spacepirates.particles.ParticleProfile;
import net.game.spacepirates.util.ComputeShader;

import java.util.Random;

public class TexturedBurstParticleSystem extends BurstParticleSystem {

    protected Vector2 size;
    public Texture colourTexture;
    public Texture maskTexture;
    public int maskChannel;

    public TexturedBurstParticleSystem(ParticleProfile profile) {
        super(profile);
    }

    @Override
    public int spawn(int offset, int amount) {
        ComputeShader program = spawnScript.program();
        program.bind();
        program.setUniform("u_startId", loc -> Gdx.gl.glUniform1i(loc, offset));
        program.setUniform("u_workload", loc -> Gdx.gl.glUniform1i(loc, 1));
        program.setUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.setUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, new Random().nextInt()));
        program.setUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        program.setUniform("u_sampleColour", loc -> {
            if(colourTexture != null) {
                colourTexture.bind(4);
                Gdx.gl.glUniform1i(loc, 4);
            }
        });
        program.setUniform("u_sampleMask", loc -> {
            if(maskTexture != null) {
                maskTexture.bind(5);
                Gdx.gl.glUniform1i(loc, 5);
            }
        });
        program.setUniform("u_sampleChannel", loc -> Gdx.gl.glUniform1i(loc, maskChannel));
        program.setUniform("u_sampleSize", loc -> Gdx.gl.glUniform2f(loc, size.x, size.y));
        uniformSetters.forEach(program::setUniform);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        int amtSpawned = round(amount, 256);
        program.dispatch(amtSpawned);
        program.unbind();
        return amtSpawned;
    }
}
