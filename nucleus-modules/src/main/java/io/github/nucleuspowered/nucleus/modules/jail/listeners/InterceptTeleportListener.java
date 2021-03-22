/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.EventContexts;
import io.github.nucleuspowered.nucleus.modules.jail.JailPermissions;
import io.github.nucleuspowered.nucleus.modules.jail.config.JailConfig;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailService;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;

public class InterceptTeleportListener implements ListenerBase.Conditional {

    private final JailService handler;
    private final IPermissionService permissionService;
    private final IMessageProviderService messageProvider;

    @Inject
    public InterceptTeleportListener(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(JailService.class);
        this.permissionService = serviceCollection.permissionService();
        this.messageProvider = serviceCollection.messageProvider();
    }

    @Listener(order = Order.LAST)
    public void onTeleport(final MoveEntityEvent event, @Getter("getEntity") final ServerPlayer player) {
        final CommandCause cause = CommandCause.create();
        final EventContext context = event.getCause().getContext();
        if (!context.get(EventContexts.BYPASS_JAILING_RESTRICTION).orElse(false) &&
                context.get(EventContexts.IS_JAILING_ACTION).orElse(false)) {
            if (this.handler.isPlayerJailed(player.getUniqueId())) {
                if (!this.permissionService.hasPermission(cause, JailPermissions.JAIL_TELEPORTJAILED)) {
                    event.setCancelled(true);
                    this.messageProvider.sendMessageTo(cause.getAudience(), "jail.abouttoteleporttarget.isjailed", player.name());
                } else if (!this.permissionService.hasPermission(cause, JailPermissions.JAIL_TELEPORTTOJAILED)) {
                    event.setCancelled(true);
                    this.messageProvider.sendMessageTo(cause.getAudience(),"jail.abouttoteleportcause.targetisjailed", player.name());
                }
            }
        }
    }

    @Override
    public boolean shouldEnable(final INucleusServiceCollection serviceCollection) {
        return serviceCollection.configProvider().getModuleConfig(JailConfig.class).aggressivelyDisableTeleportsForJailed();
    }
}
