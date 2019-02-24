package net.game.spacepirates.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisTextButton;
import net.game.spacepirates.SpacePiratesLauncher;
import net.game.spacepirates.input.InputHelper;


public class MainMenuScreen implements Screen {

    Stage stage;
    Viewport stageViewport;
    OrthographicCamera stageCamera;
    VisTextButton boottun;
    private SpacePiratesLauncher spacePiratesLauncher;

    public MainMenuScreen(SpacePiratesLauncher spacePiratesLauncher) {
        this.spacePiratesLauncher = spacePiratesLauncher;
    }

    @Override
    public void show() {
        stageCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stageViewport = new ScreenViewport(stageCamera);
        stage = new Stage(stageViewport);

        InputHelper.AddProcessors(stage);

        boottun = new VisTextButton("dis iz a bootun dat moovs u 2 gaem scren");
        boottun.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                spacePiratesLauncher.setScreen(new GameScreen(spacePiratesLauncher));
            }
        });

        stage.addActor(boottun);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        stageViewport.update(width, height, true);
        boottun.setBounds(100,100,200,100);
//      root.setBounds(0, 0, width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        InputHelper.RemoveProcessors(stage);
    }

    @Override
    public void dispose() {

    }
}
