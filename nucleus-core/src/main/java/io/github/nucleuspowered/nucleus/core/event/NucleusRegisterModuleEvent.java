package io.github.nucleuspowered.nucleus.core.event;

import io.github.nucleuspowered.nucleus.core.module.IModuleProvider;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class NucleusRegisterModuleEvent extends AbstractEvent implements RegisterModuleEvent {

    private final Cause cause;
    private final Set<IModuleProvider> providers = new HashSet<>();

    public NucleusRegisterModuleEvent(final Cause cause) {
        this.cause = cause;
    }

    @Override
    public Cause cause() {
        return this.cause;
    }

    @Override
    public void registerModuleProvider(final IModuleProvider provider) {
        this.providers.add(provider);
    }

    public Set<IModuleProvider> getProviders() {
        return Collections.unmodifiableSet(this.providers);
    }

}
