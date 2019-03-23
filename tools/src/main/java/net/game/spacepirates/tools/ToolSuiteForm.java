package net.game.spacepirates.tools;

import net.game.spacepirates.tools.api.ITool;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ToolSuiteForm {
    private JPanel rootPane;
    private JTabbedPane tabbedPane;

    private JFrame frame;

    public ToolSuiteForm(JFrame frame) {
        this.frame = frame;
        frame.setContentPane(this.rootPane);
    }

    public void addTool(ITool tool) {
        tabbedPane.addTab(tool.name(), tool.icon(), tool.rootComponent());
    }

    public static ToolSuiteForm build(String[] args) {
        JFrame frame = new JFrame("ToolSuiteForm");
        ToolSuiteForm form = new ToolSuiteForm(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        JMenuBar jMenuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("Theme");

        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo laf : lafs) {
            themeMenu.add(new AbstractAction(laf.getName()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        UIManager.setLookAndFeel(laf.getClassName());
                        SwingUtilities.updateComponentTreeUI(frame);
                    } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        jMenuBar.add(themeMenu);
        frame.setJMenuBar(jMenuBar);

        return form;
    }
}
