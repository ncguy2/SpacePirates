package net.game.spacepirates.entity.types;

import net.game.spacepirates.entity.component.InputComponent;

public class LocalPlayer extends Player {

    public InputComponent inputComponent;

    @Override
    public void init() {
        super.init();
        inputComponent = new InputComponent("Input");
    }

    @Override
    public void assemble() {
        super.assemble();
        addComponent(inputComponent);
    }
}
