/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.listeners;

import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.google.inject.Inject;

public class BasicAFKListener extends AbstractAFKListener {

    @Inject
    public BasicAFKListener(final INucleusServiceCollection serviceCollection) {
        super(serviceCollection.getServiceUnchecked(AFKHandler.class));
    }

    @Listener(order = Order.FIRST)
    public void onPlayerJoin(final ClientConnectionEvent.Join event, @Getter("getTargetEntity") final Player player) {
        update(player);
    }

}
