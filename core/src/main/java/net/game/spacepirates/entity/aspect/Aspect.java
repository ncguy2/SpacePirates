package net.game.spacepirates.entity.aspect;

import java.util.Optional;

public class Aspect<T> {

    private Object owner;
    private AspectKey<T> key;
    private T object;

    public Object getOwner() {
        return owner;
    }

    public AspectKey<T> getKey() {
        return key;
    }

    public T getObject() {
        return object;
    }

    public static <T> Aspect<T> of(Object owner, AspectKey<T> key, T object) {
        Aspect<T> aspect = new Aspect<>();
        aspect.owner = owner;
        aspect.key = key;
        aspect.object = object;
        return aspect;
    }

    public static <T> Optional<Aspect<T>> of(Object owner, AspectKey<T> key) {
        if(owner instanceof IAspectProvider) {
            return ((IAspectProvider) owner).getAspect(key);
        }
        return Optional.empty();
    }

}
