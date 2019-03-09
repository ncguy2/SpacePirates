package net.game.spacepirates.input;

public abstract class BaseInputAction {

    public String name;
    public int id;

    public BaseInputAction(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public abstract boolean test();

}
