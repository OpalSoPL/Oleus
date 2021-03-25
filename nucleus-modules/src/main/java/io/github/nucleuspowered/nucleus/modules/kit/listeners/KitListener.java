/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.core.event.NucleusFirstJoinEvent;
import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;

public class KitListener implements ListenerBase {

    private final KitService handler;

    @Inject
    public KitListener(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(KitService.class);
    }

    @Listener
    public void onPlayerFirstJoin(final NucleusFirstJoinEvent event, @Getter("getPlayer") final ServerPlayer player) {
        for (final Kit kit : this.handler.getFirstJoinKits()) {
            this.handler.redeemKit(kit, player.uniqueId(), true, true);
        }
    }

}
