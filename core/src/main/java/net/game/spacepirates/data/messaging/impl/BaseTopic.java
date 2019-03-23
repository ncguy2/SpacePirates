package net.game.spacepirates.data.messaging.impl;

import net.game.spacepirates.data.messaging.api.Topic;

import java.util.Objects;

public class BaseTopic<T> implements Topic<T> {

    private final String ref;
    private final Class<T> type;

    public BaseTopic(String ref, Class<T> type) {
        this.ref = ref;
        this.type = type;
    }

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseTopic<?> baseTopic = (BaseTopic<?>) o;
        return Objects.equals(ref, baseTopic.ref) &&
                Objects.equals(type, baseTopic.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ref, type);
    }
}
