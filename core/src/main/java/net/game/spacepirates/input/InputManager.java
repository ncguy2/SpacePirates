package net.game.spacepirates.input;

public class InputManager {

    private static InputManager instance;
    public static InputManager instance() {
        if (instance == null)
            instance = new InputManager();
        return instance;
    }

    private InputManager() {}

    public float _scale(float value, InputAction action) {
        return value * _query(action);
    }

    public float _scale(float value, InputAxis axis) {
        return value * _query(axis);
    }

    public float _query(InputAction action) {
        return action.test() ? 1 : 0;
    }

    public float _query(InputAxis axis) {
        return axis.resolve();
    }

    public boolean _IsPressed(InputAction action) {
        return action.test();
    }

    public boolean _IsPressed(InputAxis axis) {
        return axis.resolve() != 0;
    }

    public static float scale(float value, InputAction action) {
        return instance()._scale(value, action);
    }

    public static float scale(float value, InputAxis axis) {
        return instance()._scale(value, axis);
    }

    public static float query(InputAction action) {
        return instance()._query(action);
    }

    public static float query(InputAxis axis) {
        return instance()._query(axis);
    }

    public static boolean isPressed(InputAction action) {
        return instance()._IsPressed(action);
    }

    public static boolean isPressed(InputAxis axis) {
        return instance()._IsPressed(axis);
    }


}
