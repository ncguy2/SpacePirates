package net.game.spacepirates.entity.aspect;

import java.util.Optional;

public interface IAspectProvider {

    default <T> Optional<Aspect<T>> getAspect(AspectKey<T> key) {
        return Optional.ofNullable(provideAspect(key));
    }

    <T> Aspect<T> provideAspect(AspectKey<T> key);

}
