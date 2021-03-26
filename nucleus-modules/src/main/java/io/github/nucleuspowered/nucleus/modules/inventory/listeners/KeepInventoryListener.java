/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.inventory.listeners;

import io.github.nucleuspowered.nucleus.modules.inventory.InventoryPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;

import com.google.inject.Inject;
import org.spongepowered.api.event.filter.Getter;

public final class KeepInventoryListener implements ListenerBase {

    private final IPermissionService permissionService;

    @Inject
    public KeepInventoryListener(final INucleusServiceCollection serviceCollection) {
        this.permissionService = serviceCollection.permissionService();
    }

    @Listener
    public void onEntityDeath(final DestructEntityEvent.Death event, @Getter("entity") final ServerPlayer player) {
        if (this.permissionService.hasPermission(player, InventoryPermissions.INVENTORY_KEEP)) {
            event.setKeepInventory(true);
        }
    }

}
