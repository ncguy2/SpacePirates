package net.game.spacepirates.particles.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import net.game.spacepirates.asset.AssetHandler;
import net.game.spacepirates.particles.ParticleProfile;
import net.game.spacepirates.util.ComputeShader;

import java.util.Random;

public class TexturedTemporalParticleSystem extends TemporalParticleSystem {

    public transient Texture texture;
    protected transient boolean isLoadingTexture = false;

    public Vector2 size;
    public String texturePath;
    public int maskChannel;

    public TexturedTemporalParticleSystem(ParticleProfile profile) {
        super(profile);
        size = profile.size;
        texturePath = profile.texturePath;
        maskChannel = profile.maskChannel;
    }

    @Override
    public int spawn(int offset, int amount) {
        ComputeShader program = spawnScript.program();
        program.bind();

        program.setUniform("u_spawnMatrix", loc -> {
            Matrix3 p;
            if(spawnMatrixSupplier == null)
                p = new Matrix3();
            else p = spawnMatrixSupplier.get();
            Gdx.gl.glUniformMatrix3fv(loc, 1, false, p.val, 0);
        });

        program.setUniform("u_startId", loc -> Gdx.gl.glUniform1i(loc, offset));
        program.setUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.setUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, new Random().nextInt()));
        program.setUniform("imaxParticleCount", loc -> Gdx.gl.glUniform1i(loc, desiredAmount));
        program.setUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        program.setUniform("u_spawnTexture", loc -> {
            if(texture != null) {
                texture.bind(4);
                Gdx.gl.glUniform1i(loc, 4);
            }
        });
        program.setUniform("u_sampleChannel", loc -> Gdx.gl.glUniform1i(loc, maskChannel));
        program.setUniform("u_size", loc -> {
            if(size != null)
                Gdx.gl.glUniform2f(loc, size.x, size.y);
        });
        uniformSetters.forEach(program::setUniform);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        int amtSpawned = round(amount, INVOCATIONS_PER_WORKGROUP);
        program.dispatch(amtSpawned);
        program.unbind();
        return amtSpawned;
    }

    @Override
    public void update(float delta) {

        if(!isLoadingTexture && texture == null && (texturePath != null && !texturePath.isEmpty())) {
            isLoadingTexture = true;
            AssetHandler.instance().GetAsync(texturePath, Texture.class, t -> {
                texture = t;
                if(size == null) {
                    size = new Vector2(texture.getWidth(), texture.getHeight());
                }
                isLoadingTexture = false;
            });
        }

        super.update(delta);
    }
}
