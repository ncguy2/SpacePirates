package net.game.spacepirates.tools;


import com.bulenkov.darcula.DarculaLaf;
import net.game.spacepirates.tools.impl.particle.ParticleProfileTool;

import javax.swing.*;
import java.io.File;
import java.util.function.Consumer;

public class ToolSuite {

    public static void main(String[] args) {
        UIManager.installLookAndFeel("Darcula", DarculaLaf.class.getCanonicalName());
        try {
            UIManager.setLookAndFeel(DarculaLaf.class.getCanonicalName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ToolSuiteForm form = ToolSuiteForm.build(args);

            form.addTool(new ParticleProfileTool());

        });
    }

    private static JFileChooser fxChooser;

    private static JFileChooser getFileChooser() {
        if (fxChooser == null) {
            fxChooser = new JFileChooser();
            fxChooser.setCurrentDirectory(new File("."));
        }
        return fxChooser;
    }

    public static void withFileChooser(Consumer<JFileChooser> task) {
        SwingUtilities.invokeLater(() -> task.accept(getFileChooser()));
    }

    public static void selectFile(String title, Consumer<File> task) {
        withFileChooser(fc -> {
            fc.setDialogTitle(title);
            fc.setMultiSelectionEnabled(false);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int i = fc.showOpenDialog(null);

            if(i == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if(file != null) {
                    task.accept(file);
                }
            }
        });
    }


}
