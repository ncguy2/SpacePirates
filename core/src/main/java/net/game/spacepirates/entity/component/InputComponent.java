package net.game.spacepirates.entity.component;

import com.badlogic.gdx.Input;
import net.game.spacepirates.input.InputAction;
import net.game.spacepirates.input.InputAxis;
import net.game.spacepirates.input.ScrollInputHelper;

public class InputComponent extends EntityComponent<InputComponent> {

    public InputAction keyFire = InputAction.key("Fire", Input.Keys.E);

    public InputAxis axisVertical = InputAxis.create()
            .add(InputAction.key("Up", Input.Keys.W), 1)
            .add(InputAction.key("Down", Input.Keys.S), -1);

    public InputAxis axisHorizontal = InputAxis.create()
            .add(InputAction.key("Left", Input.Keys.A), -1)
            .add(InputAction.key("Right", Input.Keys.D), 1);

    public InputAxis zoomAxis = InputAxis.create()
            .add(InputAction.scroll("Zoom in", ScrollInputHelper.ScrollType.UP), 1)
            .add(InputAction.scroll("Zoom out", ScrollInputHelper.ScrollType.DOWN), -1);

    public InputComponent(String name) {
        super(name);
    }

}
