/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.listeners;

import io.github.nucleuspowered.nucleus.modules.afk.config.AFKConfig;
import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

import com.google.inject.Inject;

public class AFKFullMoveListener extends AbstractAFKListener implements ListenerBase.Conditional {

    @Inject
    public AFKFullMoveListener(final INucleusServiceCollection collection) {
        super(collection.getServiceUnchecked(AFKHandler.class));
    }

    @Listener(order = Order.LAST)
    public void onPlayerMove(final MoveEntityEvent event, @Root final Player player) {
        update(player);
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        final AFKConfig.Triggers triggers = serviceCollection.configProvider().getModuleConfig(AFKConfig.class)
                .getTriggers();
        return triggers.isOnMovement() && triggers.isOnRotation();
    }

}
