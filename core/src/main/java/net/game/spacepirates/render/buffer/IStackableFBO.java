package net.game.spacepirates.render.buffer;

public interface IStackableFBO {

    void beginFBO();
    void endFBO();

    String name();

}
