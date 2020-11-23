/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.back.listeners;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.module.jail.NucleusJailService;
import io.github.nucleuspowered.nucleus.modules.back.BackPermissions;
import io.github.nucleuspowered.nucleus.modules.back.config.BackConfig;
import io.github.nucleuspowered.nucleus.modules.back.services.BackHandler;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.ChangeEntityWorldEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.type.Exclude;

import com.google.inject.Inject;
import org.spongepowered.api.world.ServerLocation;

public class BackListeners implements IReloadableService.Reloadable, ListenerBase {

    private final BackHandler handler;
    private final IPermissionService permissionService;
    private BackConfig backConfig = new BackConfig();
    @Nullable private final NucleusJailService jailService;

    @Inject
    public BackListeners(final INucleusServiceCollection serviceCollection) {
        // TODO: Pluggable stuff.
        this.jailService = NucleusAPI.getJailService().orElse(null);
        this.handler = serviceCollection.getServiceUnchecked(BackHandler.class);
        this.permissionService = serviceCollection.permissionService();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.backConfig = serviceCollection.configProvider().getModuleConfig(BackConfig.class);
    }

    @Listener(order = Order.LAST)
    @Exclude(ChangeEntityWorldEvent.Reposition.class)
    public void onTeleportPlayer(final MoveEntityEvent event, @Getter("getEntity") final ServerPlayer pl) {
        if (this.backConfig.isOnTeleport() && this.check(event) &&
                this.getLogBack(pl) && this.permissionService.hasPermission(pl,
                BackPermissions.BACK_ONTELEPORT)) {
            this.handler.setLastLocation(pl.getUniqueId(), ServerLocation.of(pl.getWorld(), event.getOriginalPosition()), pl.getRotation());
        }
    }

    @Listener(order = Order.LAST)
    public void onWorldTransfer(final ChangeEntityWorldEvent.Reposition event, @Getter("getEntity") final ServerPlayer pl) {
        if (this.backConfig.isOnPortal() && this.getLogBack(pl) && this.permissionService.hasPermission(pl, BackPermissions.BACK_ONPORTAL)) {
            this.handler.setLastLocation(pl.getUniqueId(), ServerLocation.of(event.getOriginalWorld(), event.getDestinationPosition()), pl.getRotation());
        }
    }

    @Listener
    public void onDeathEvent(final DestructEntityEvent.Death event, @Getter("getEntity") final ServerPlayer pl) {
        if (this.backConfig.isOnDeath() && this.getLogBack(pl) && this.permissionService.hasPermission(pl, BackPermissions.BACK_ONDEATH)) {
            this.handler.setLastLocation(pl.getUniqueId(), pl.getServerLocation(), pl.getRotation());
        }
    }

    private boolean check(final MoveEntityEvent event) {
        return !event.getOriginalPosition().equals(event.getDestinationPosition());
    }

    private boolean getLogBack(final ServerPlayer player) {
        return !(this.jailService != null && this.jailService.isPlayerJailed(player.getUniqueId())) && this.handler.isLoggingLastLocation(player.getUniqueId());
    }
}
