package net.game.spacepirates.util;

import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.EntityComponent;
import net.game.spacepirates.entity.component.SceneComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EntityUtils {

    public static Stream<EntityComponent> flattenComponents(Entity entity) {
        return flattenComponents(entity.rootComponent);
    }

    public static Stream<EntityComponent> flattenComponents(SceneComponent parentComponent) {
        List<EntityComponent> componentList = new ArrayList<>();
        flattenComponent(parentComponent, componentList);
        return componentList.stream();
    }

    private static void flattenComponent(EntityComponent<?> component, List<EntityComponent> componentList) {
        componentList.add(component);

        if(component instanceof SceneComponent) {
            for (EntityComponent<?> c : ((SceneComponent<?>) component).components) {
                flattenComponent(c, componentList);
            }
        }
    }

}
