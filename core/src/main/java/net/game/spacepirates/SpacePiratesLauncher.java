package net.game.spacepirates;

import com.badlogic.gdx.Game;
import net.game.spacepirates.screen.FirstScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class SpacePiratesLauncher extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}