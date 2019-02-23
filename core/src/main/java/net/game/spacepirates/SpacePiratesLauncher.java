package net.game.spacepirates;

import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;
import net.game.spacepirates.screen.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SpacePiratesLauncher extends Game {
    @Override
    public void create() {
        VisUI.load();
        setScreen(new MainMenuScreen());
    }
}