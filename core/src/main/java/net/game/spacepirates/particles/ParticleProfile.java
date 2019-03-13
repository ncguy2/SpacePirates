package net.game.spacepirates.particles;

import com.badlogic.gdx.math.Vector2;
import net.game.spacepirates.particles.system.AbstractParticleSystem;
import net.game.spacepirates.util.curve.GLColourCurve;

public class ParticleProfile {

    public String name;

    public SystemType type;

    public String[] blocks;

    public GLColourCurve curve;
    public float duration;
    public int particleCount;

    public LoopingBehaviour loopingBehaviour = LoopingBehaviour.None;
    public int loopingAmount = 1;

    // Temporal only
    public float spawnOverTime;

    // Textured only
    public String texturePath;
    public int maskChannel;
    public Vector2 size;

    public AbstractParticleSystem create() {
        return type.create(this);
    }

}
