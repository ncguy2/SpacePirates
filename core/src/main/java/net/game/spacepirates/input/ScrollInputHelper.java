package net.game.spacepirates.input;

import com.badlogic.gdx.InputAdapter;

public class ScrollInputHelper extends InputAdapter {

    private static ScrollInputHelper instance;
    public static ScrollInputHelper instance() {
        if (instance == null)
            instance = new ScrollInputHelper();
        return instance;
    }

    protected int scrollAmt;

    private ScrollInputHelper() {
        InputHelper.AddProcessors(this);
    }

    public void update(float delta) {
        scrollAmt = 0;
    }

    public int getScrollAmount() {
        return scrollAmt;
    }

    public static boolean resolve(int typeId) {
        return instance()._resolve(ScrollType.values()[typeId]);
    }
    public boolean _resolve(ScrollType type) {
        switch(type) {
            case ANY:
                return scrollAmt != 0;
            case UP:
                return scrollAmt > 0;
            case DOWN:
                return scrollAmt < 0;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        scrollAmt = amount;
        return super.scrolled(amount);
    }

    public static enum ScrollType {
        ANY,    // Any scroll amount
        UP,     // Positive scroll amount
        DOWN,   // Negative scroll amount
    }

}
