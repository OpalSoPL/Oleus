/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nameban.listeners;

import io.github.nucleuspowered.nucleus.modules.nameban.services.NameBanHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import com.google.inject.Inject;

public class NameBanListener implements ListenerBase {

    private final NameBanHandler nameBanHandler;

    @Inject
    public NameBanListener(final INucleusServiceCollection serviceCollection) {
        this.nameBanHandler = serviceCollection.getServiceUnchecked(NameBanHandler.class);
    }

    @Listener
    public void onPlayerLogin(final ServerSideConnectionEvent.Auth event) {
        event.getProfile().getName().flatMap(name -> this.nameBanHandler.getReasonForBan(name.toLowerCase())).ifPresent(x -> {
            event.setCancelled(true);
            event.setMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(x));
        });
    }
}
