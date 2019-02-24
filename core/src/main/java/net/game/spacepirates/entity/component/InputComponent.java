package net.game.spacepirates.entity.component;

import com.badlogic.gdx.Input;
import net.game.spacepirates.input.InputAction;
import net.game.spacepirates.input.InputAxis;
import net.game.spacepirates.input.ScrollInputHelper;

public class InputComponent extends EntityComponent<InputComponent> {

    public InputAction keyUp = InputAction.key("Up", Input.Keys.W);
    public InputAction keyDown = InputAction.key("Down", Input.Keys.S);
    public InputAction keyLeft = InputAction.key("Left", Input.Keys.A);
    public InputAction keyRight = InputAction.key("Right", Input.Keys.D);
    public InputAction keyFire = InputAction.key("Fire", Input.Keys.E);

    public InputAxis zoomAxis = InputAxis.create()
            .add(InputAction.scroll("Zoom in", ScrollInputHelper.ScrollType.UP), 1)
            .add(InputAction.scroll("Zoom out", ScrollInputHelper.ScrollType.DOWN), -1);

    public InputComponent(String name) {
        super(name);
    }

}
