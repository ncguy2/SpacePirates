package net.game.spacepirates.tools.impl.particle;

import net.game.spacepirates.particles.ParticleProfile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProfileComponent {

    private final ParticleProfile profile;
    private JPanel rootPanel;
    private JTable dataTable;
    private JTable colourTable;
    private JPanel colourParent;
    private JPanel dataParent;
    private JPanel colourSidebar;

    public ProfileComponent(ParticleProfile profile) {
        this.profile = profile;


        DefaultTableModel dataModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        dataModel.addColumn("Key");
        dataModel.addColumn("Value");

        dataModel.addRow(new Object[] { "Name", profile.name});
        dataModel.addRow(new Object[] { "Type", profile.type});
        dataModel.addRow(new Object[] { "Duration", profile.duration});
        dataModel.addRow(new Object[] { "Particle Count", profile.particleCount});
        dataModel.addRow(new Object[] { "Looping Behaviour", profile.loopingBehaviour});
        dataModel.addRow(new Object[] { "Looping Amount", profile.loopingAmount});
        dataModel.addRow(new Object[] { "spawnOverTime", profile.spawnOverTime});
        dataModel.addRow(new Object[] { "Texture path", profile.texturePath});
        dataModel.addRow(new Object[] { "Mask channel", profile.maskChannel});
        dataModel.addRow(new Object[] { "Size", profile.size});

        dataTable.setModel(dataModel);

    }

    public Component rootComponent() {
        return rootPanel;
    }
}
