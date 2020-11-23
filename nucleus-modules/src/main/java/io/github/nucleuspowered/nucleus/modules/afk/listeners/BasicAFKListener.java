/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.listeners;

import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;

import com.google.inject.Inject;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class BasicAFKListener extends AbstractAFKListener {

    @Inject
    public BasicAFKListener(final INucleusServiceCollection serviceCollection) {
        super(serviceCollection.getServiceUnchecked(AFKHandler.class));
    }

    @Listener(order = Order.FIRST)
    public void onPlayerJoin(final ServerSideConnectionEvent.Join event, @Getter("getPlayer") final ServerPlayer player) {
        this.update(player);
    }

}
