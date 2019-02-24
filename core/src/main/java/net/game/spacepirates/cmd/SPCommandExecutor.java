package net.game.spacepirates.cmd;

import com.badlogic.gdx.Screen;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.HiddenCommand;
import net.game.spacepirates.SpacePiratesLauncher;
import net.game.spacepirates.screen.GameScreen;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SPCommandExecutor extends CommandExecutor {

    private final SpacePiratesLauncher spacePiratesLauncher;

    public SPCommandExecutor(SpacePiratesLauncher spacePiratesLauncher) {
        this.spacePiratesLauncher = spacePiratesLauncher;
    }

    public void screen(String screenName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> cls = Class.forName(GameScreen.class.getPackage().getName() + "." + screenName);
        if(cls == null) {
            console.log("No screen found with name: " + screenName);
            return;
        }

        Constructor<?> ctor = cls.getConstructor(SpacePiratesLauncher.class);
        if(ctor == null) {
            console.log("No valid constructor found in: " + cls.getCanonicalName());
            return;
        }

        Object obj = ctor.newInstance(spacePiratesLauncher);
        if (!(obj instanceof Screen)) {
            console.log(obj.getClass().getCanonicalName() + " is not a valid screen");
            return;
        }
        Screen screen = (Screen) obj;
        spacePiratesLauncher.setScreen(screen);
    }

    public void screen() {
        console.log("Accessible screens:");
        console.log("    GameScreen");
        console.log("    MainMenuScreen");
    }

    @HiddenCommand
    public void cls() {
        console.clear();
    }

    public void reinitRenderer() {
        Screen screen = spacePiratesLauncher.getScreen();
        if(screen instanceof GameScreen) {
            ((GameScreen) screen).getRenderer().reInit();
        }
    }

}
