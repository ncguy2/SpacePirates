package net.game.spacepirates.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ShapeRenderHost {

    private static ShapeRenderHost instance;
    public static ShapeRenderHost get() {
        if(instance == null) {
            instance = new ShapeRenderHost();
        }
        return instance;
    }

    private final List<ShapeRenderInfo> renderInfos;
    private ShapeRenderer renderer;

    public ShapeRenderHost() {
        renderInfos = new CopyOnWriteArrayList<>();
        Gdx.app.postRunnable(() -> renderer = new ShapeRenderer());
    }

    public void add(ShapeRenderInfo info) {
        renderInfos.add(info);
    }

    public void draw(Matrix4 projection) {
        if(renderer == null || renderInfos.isEmpty()) {
            return;
        }
        renderer.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        renderer.setAutoShapeType(true);
        renderer.begin();
        renderInfos.stream().filter(i -> i.type == ShapeRenderer.ShapeType.Filled).forEach(this::drawInfo);
        renderInfos.stream().filter(i -> i.type == ShapeRenderer.ShapeType.Line).forEach(this::drawInfo);
        renderInfos.stream().filter(i -> i.type == ShapeRenderer.ShapeType.Point).forEach(this::drawInfo);
        renderer.end();

        renderInfos.removeIf(i -> i.durationRemaining <= 0);
    }

    public void drawInfo(ShapeRenderInfo info) {
        renderer.set(info.type);
        renderer.setColor(info.colour);
        info.task.accept(renderer);
        info.durationRemaining -= Gdx.graphics.getDeltaTime();
    }

    public static class ShapeRenderInfo {

        public Color colour;
        public float durationRemaining;
        public ShapeRenderer.ShapeType type;
        public Consumer<ShapeRenderer> task;

    }

}
