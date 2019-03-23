package net.game.spacepirates.tools.impl.particle;

import net.game.spacepirates.particles.ParticleProfile;
import net.game.spacepirates.tools.ToolSuite;
import net.game.spacepirates.util.io.Json;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class ParticleProfileForm {
    private JPanel rootPanel;
    private JList<ParticleProfile> profileList;
    private JPanel controlRoot;
    private JTextField fileRoot;
    private JButton browseBtn;

    public ParticleProfileForm() {
        browseBtn.addActionListener(e -> {
            ToolSuite.selectFile("Particle profile root", file -> {
                SwingUtilities.invokeLater(() -> this.selectFile(file));
            });
        });
        fileRoot.addActionListener(e -> {
            String text = fileRoot.getText();
            if(text.isEmpty()) {
                return;
            }

            File f = new File(text);
            if(f.exists()) {
                selectFile(f);
            }
        });
        profileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileList.addListSelectionListener(e -> selectProfile(profileList.getSelectedValue()));
    }

    public void selectProfile(ParticleProfile profile) {
        ProfileComponent component = new ProfileComponent(profile);
        for (Component c : controlRoot.getComponents()) {
            controlRoot.remove(c);
        }
        Component comp = component.rootComponent();
        controlRoot.add(comp, BorderLayout.CENTER);
    }

    public void selectFile(File file) {
        if(file.isFile()) {
            file = file.getParentFile();
        }

        fileRoot.setText(file.getAbsolutePath());


        DefaultListModel<ParticleProfile> model = new DefaultListModel<>();
        File[] files = file.listFiles((dir, name) -> name.endsWith(".json"));
        if(files == null || files.length == 0) {
            return;
        }

        Arrays.stream(files)
              .map(f -> Json.from(f, ParticleProfile.class))
              .forEach(model::addElement);

        profileList.setModel(model);
    }

    public JComponent rootComponent() {
        return rootPanel;
    }
}
