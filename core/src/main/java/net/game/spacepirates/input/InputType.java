package net.game.spacepirates.input;

import com.badlogic.gdx.Gdx;

import java.util.function.Function;

public enum InputType {
    Keyboard(Gdx.input::isKeyPressed),
    Mouse(Gdx.input::isButtonPressed),
    Scroll(ScrollInputHelper::resolve);

    private final Function<Integer, Boolean> isPressedFunc;
    InputType(Function<Integer, Boolean> isPressedFunc) {
        this.isPressedFunc = isPressedFunc;
    }

    public boolean test(int id) {
        return isPressedFunc.apply(id);
    }

}
