package net.game.spacepirates.tools.impl.particle;

import net.game.spacepirates.tools.api.ITool;

import javax.swing.*;

public class ParticleProfileTool implements ITool {

    @Override
    public String name() {
        return "Particle Profile Editor";
    }

    @Override
    public Icon icon() {
        return null;
    }

    @Override
    public JComponent rootComponent() {
        return new ParticleProfileForm().rootComponent();
    }
}
