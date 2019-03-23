package net.game.spacepirates.data.messaging.api;

public interface Topic<T> {

    String getRef();
    Class<T> getType();

}
