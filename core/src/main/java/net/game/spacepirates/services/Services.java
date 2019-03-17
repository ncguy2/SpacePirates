package net.game.spacepirates.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Services {

    private static Map<Class<? extends BaseService>, BaseService> serviceMap = new HashMap<>();

    public static <T extends BaseService> T get(Class<T> type) {
        return Optional.ofNullable(serviceMap.get(type))
                .map(type::cast)
                .orElse(null);
    }

    public static void registerService(BaseService service) {
        if(serviceMap.containsKey(service.getClass())) {
            throw new IllegalStateException("A service of type \"" + service.getClass().getCanonicalName() + "\" is already registered");
        }

        serviceMap.put(service.getServiceClass(), service);
    }

    public static <T extends BaseService> T registerService(Class<T> type) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        if(serviceMap.containsKey(type)) {
            throw new IllegalStateException("A service of type \"" + type.getCanonicalName() + "\" is already registered");
        }

        Constructor<T> ctor = type.getConstructor();
        return ctor.newInstance();
    }
}
