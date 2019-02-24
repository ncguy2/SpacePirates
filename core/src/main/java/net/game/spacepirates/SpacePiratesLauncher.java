package net.game.spacepirates;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.strongjoshua.console.GUIConsole;
import net.game.spacepirates.asset.AssetHandler;
import net.game.spacepirates.cmd.SPCommandExecutor;
import net.game.spacepirates.input.InputHelper;
import net.game.spacepirates.screen.MainMenuScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class SpacePiratesLauncher extends Game {

    private OrthographicCamera globalCamera;
    private ScreenViewport globalViewport;
    private Stage globalStage;
    private GUIConsole console;


    @Override
    public void create() {
        ShaderProgram.pedantic = false;
        VisUI.load();
        globalCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        globalViewport = new ScreenViewport(globalCamera);
        globalStage = new Stage(globalViewport);

        console = new GUIConsole(VisUI.getSkin());
        console.setPositionPercent(0, 100);
        console.setSizePercent(100, 30);
        console.setCommandExecutor(new SPCommandExecutor(this));

        InputHelper.AddProcessors(console.getInputProcessor());

        console.setDisplayKeyID(Input.Keys.RIGHT_BRACKET);

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        AssetHandler.instance().Update();
        super.render();

        globalStage.act(Gdx.graphics.getDeltaTime());
        globalStage.draw();
        console.setPositionPercent(0, 100);
        console.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            // Window minimised
            return;
        }

        super.resize(width, height);
        globalViewport.update(width, height, true);
        console.refresh();
    }
}