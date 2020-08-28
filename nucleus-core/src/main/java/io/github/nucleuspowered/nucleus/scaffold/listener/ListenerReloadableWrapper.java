package io.github.nucleuspowered.nucleus.scaffold.listener;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;

public class ListenerReloadableWrapper implements IReloadableService.Reloadable {

    private final ListenerBase.Conditional listenerBase;

    public ListenerReloadableWrapper(final ListenerBase.Conditional listenerBase) {
        this.listenerBase = listenerBase;
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        Sponge.getEventManager().unregisterListeners(this.listenerBase);
        if (this.listenerBase.shouldEnable(serviceCollection)) {
            Sponge.getEventManager().registerListeners(serviceCollection.pluginContainer(), this.listenerBase);
        }

    }
}
