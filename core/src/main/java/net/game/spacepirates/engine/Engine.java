package net.game.spacepirates.engine;

import net.game.spacepirates.system.AbstractSystem;

import java.util.ArrayList;
import java.util.List;

public class Engine {

    private final List<AbstractSystem> systems;

    public Engine() {
        systems = new ArrayList<>();
    }

    public void addSystem(AbstractSystem system) {
        system.attachTo(this);
        this.systems.add(system);
        try{
            system.startup();
        }catch(Throwable e) {
            e.printStackTrace();
            system.setIsActive(false);
        }
    }

    public void removeSystem(AbstractSystem system) {
        try{
            system.shutdown();
        }catch (Throwable e) {
            e.printStackTrace();
            system.setIsActive(false);
        }
        this.systems.remove(system);
        system.detach();
    }

    public void update(float delta) {
        systems.forEach(sys -> updateSystem(delta, sys));
    }

    private void updateSystem(float delta, AbstractSystem sys) {
        try{
            sys.update(delta);
        }catch(Throwable e) {
            e.printStackTrace();
            sys.setIsActive(false);
        }
    }

}
