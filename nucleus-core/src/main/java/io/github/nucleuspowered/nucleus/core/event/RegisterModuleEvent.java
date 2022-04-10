package io.github.nucleuspowered.nucleus.core.event;

import io.github.nucleuspowered.nucleus.core.module.IModuleProvider;
import org.spongepowered.api.event.Event;

public interface RegisterModuleEvent extends Event {

    void registerModuleProvider(final IModuleProvider provider);

}
