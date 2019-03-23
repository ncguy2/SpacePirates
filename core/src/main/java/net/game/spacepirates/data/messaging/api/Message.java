package net.game.spacepirates.data.messaging.api;

public interface Message<T> {

    String getRef();
    T getData();

}
