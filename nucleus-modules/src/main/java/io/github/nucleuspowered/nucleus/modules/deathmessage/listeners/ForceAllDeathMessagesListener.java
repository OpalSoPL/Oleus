/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.deathmessage.listeners;

import io.github.nucleuspowered.nucleus.modules.deathmessage.config.DeathMessageConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;

public class ForceAllDeathMessagesListener implements ListenerBase.Conditional {

    @Listener(order = Order.LATE)
    public void onDeath(final DestructEntityEvent.Death event, @Getter("getEntity") final Living living) {
        if (living instanceof Player) {
            event.setAudience(Sponge.getServer());
        }
    }

    @Override public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        final DeathMessageConfig deathMessageConfig = serviceCollection.configProvider().getModuleConfig(DeathMessageConfig.class);
        return deathMessageConfig.isEnableDeathMessages() && deathMessageConfig.isForceForAll();
    }

}