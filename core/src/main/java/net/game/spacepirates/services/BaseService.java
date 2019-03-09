package net.game.spacepirates.services;

public abstract class BaseService {

    public BaseService() {
        Services.registerService(this);
    }

    public abstract String name();

    public Class<? extends BaseService> getServiceClass() {
        return getClass();
    }

}
