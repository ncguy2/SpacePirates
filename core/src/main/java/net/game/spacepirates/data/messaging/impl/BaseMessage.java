package net.game.spacepirates.data.messaging.impl;

import net.game.spacepirates.data.messaging.api.Message;

public class BaseMessage<T> implements Message<T> {

    public final String ref;
    public final T data;

    public BaseMessage(String ref, T data) {
        this.ref = ref;
        this.data = data;
    }

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public T getData() {
        return data;
    }
}
