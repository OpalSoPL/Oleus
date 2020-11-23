/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.listeners;

import io.github.nucleuspowered.nucleus.modules.afk.config.AFKConfig;
import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.function.Predicate;

abstract class AbstractAFKListener implements ListenerBase {

    private final AFKHandler handler;

    protected AbstractAFKListener(final AFKHandler handler) {
        this.handler = handler;
    }

    final void update(final ServerPlayer player) {
        this.handler.stageUserActivityUpdate(player);
    }

    final boolean getTriggerConfigEntry(final AFKConfig config, final Predicate<AFKConfig.Triggers> triggersPredicate) {
        return triggersPredicate.test(config.getTriggers());
    }
}
