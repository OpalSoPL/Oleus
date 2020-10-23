/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.events;

import io.github.nucleuspowered.nucleus.api.module.home.event.NucleusHomeEvent;
import io.github.nucleuspowered.nucleus.scaffold.event.AbstractCancelMessageEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.world.ServerLocation;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractHomeEvent extends AbstractCancelMessageEvent implements NucleusHomeEvent {

    private final String name;
    private final UUID owner;
    private final ServerLocation location;

    private boolean isCancelled = false;

    AbstractHomeEvent(final String name, final UUID owner, final Cause cause, @Nullable final ServerLocation location) {
        super(cause);
        this.name = name;
        this.owner = owner;
        this.location = location;
    }

    @Override public String getName() {
        return this.name;
    }

    @Override public UUID getUser() {
        return this.owner;
    }

    @Override public Optional<ServerLocation> getLocation() {
        return Optional.ofNullable(this.location);
    }

    @Override public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }
}
