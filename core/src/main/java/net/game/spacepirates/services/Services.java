package net.game.spacepirates.services;

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

}
