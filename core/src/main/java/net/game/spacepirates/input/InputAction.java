package net.game.spacepirates.input;

public class InputAction {

    public String name;
    public int id;
    public InputType type;

    public static InputAction key(String name, int keyId) {
        return new InputAction(name, keyId, InputType.Keyboard);
    }

    public static InputAction button(String name, int buttonId) {
        return new InputAction(name, buttonId, InputType.Mouse);
    }

    public static InputAction scroll(String name, ScrollInputHelper.ScrollType scrollType) {
        return new InputAction(name, scrollType.ordinal(), InputType.Scroll);
    }

    public InputAction(String name, int id, InputType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public boolean test() {
        return type.test(id);
    }
}
