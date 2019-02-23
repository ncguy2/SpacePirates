package net.game.spacepirates.render;

import net.game.spacepirates.entity.component.RenderComponent;

import java.util.List;

public abstract class AbstractRenderer {

    public abstract void init();
    public abstract void renderProxies(List<RenderComponent.RenderProxy> proxyList);
    public abstract void resize(int width, int height);

}
