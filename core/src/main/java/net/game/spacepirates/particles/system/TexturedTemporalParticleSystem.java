package net.game.spacepirates.particles.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import net.game.spacepirates.asset.AssetHandler;
import net.game.spacepirates.particles.ParticleBuffer;
import net.game.spacepirates.particles.ParticleProfile;
import net.game.spacepirates.util.ComputeShader;

public class TexturedTemporalParticleSystem extends TemporalParticleSystem {

    private final Vector2 size;
    private Texture colourTexture;
    private Texture spawnTexture;
    private int spawnMaskChannel;

    public TexturedTemporalParticleSystem(ParticleProfile profile, ParticleBuffer buffer) {
        super(profile, buffer);
        size = new Vector2(profile.size);
        setSpawnTextureRef(profile.texturePath);
        setSpawnMaskChannel(profile.maskChannel);
    }

    public TexturedTemporalParticleSystem setSpawnMaskChannel(int spawnMaskChannel) {
        this.spawnMaskChannel = spawnMaskChannel;
        return this;
    }

    public void setSpawnTextureRef(String ref) {
        spawnTexture = null;
        if(ref == null || ref.isEmpty()) {
            return;
        }

        AssetHandler.get().GetAsync(ref, Texture.class, this::setSpawnTexture);
    }

    public void setColourTextureRef(String ref) {
        colourTexture = null;
        if (ref == null || ref.isEmpty()) {
            return;
        }

        AssetHandler.get().GetAsync(ref, Texture.class, this::setColourTexture);
    }

    public void setSpawnTexture(Texture spawnTexture) {
        this.spawnTexture = spawnTexture;
    }
    
    public void setColourTexture(Texture colourTexture) {
        this.colourTexture = colourTexture;
    }

    public void setSize(float x, float y) {
        size.set(x, y);
    }

    public void setSize(Vector2 size) {
        setSize(size.x, size.y);
    }

    @Override
    public void updateSystem(float delta) {
        if (colourTexture == null || spawnTexture == null) {
            return;
        }

        super.updateSystem(delta);
    }

    @Override
    public void setSpawnUniforms(ComputeShader program) {
        super.setSpawnUniforms(program);

        program.setUniform("u_spawnTexture", loc -> {
            if (spawnTexture != null) {
                spawnTexture.bind(4);
                Gdx.gl.glUniform1i(loc, 4);
                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            }
        });

        program.setUniform("u_sampleChannel", loc -> Gdx.gl.glUniform1i(loc, spawnMaskChannel));
        program.setUniform("u_size", loc -> {
            if (size != null)
                Gdx.gl.glUniform2f(loc, size.x, size.y);
        });
    }

    @Override
    public void setCommonUniforms(int[] indices, ComputeShader program, float delta) {
        super.setCommonUniforms(indices, program, delta);

        program.setUniform("u_colourTexture", loc -> {
            if (colourTexture != null) {
                colourTexture.bind(5);
                Gdx.gl.glUniform1i(loc, 5);
                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            }
        });
    }
}
