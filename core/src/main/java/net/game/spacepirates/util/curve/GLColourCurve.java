package net.game.spacepirates.util.curve;

import com.badlogic.gdx.graphics.Color;

public class GLColourCurve extends Curve<Color> {

    public GLColourCurve() {
        super(Color.class);
    }

    @Override
    public Color Interp(Color a, Color b, float normalized) {
        return a.cpy().lerp(b, normalized);
    }



}
