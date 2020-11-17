/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.listeners;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.EventContexts;
import io.github.nucleuspowered.nucleus.api.module.jail.data.Jail;
import io.github.nucleuspowered.nucleus.api.module.jail.data.Jailing;
import io.github.nucleuspowered.nucleus.api.teleport.event.NucleusTeleportEvent;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import io.github.nucleuspowered.nucleus.core.events.NucleusOnLoginEvent;
import io.github.nucleuspowered.nucleus.modules.jail.JailPermissions;
import io.github.nucleuspowered.nucleus.modules.jail.config.JailConfig;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailService;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailingEntry;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.util.PermissionMessageChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import java.util.List;
import java.util.Optional;

public class JailListener implements IReloadableService.Reloadable, ListenerBase {

    private final IPermissionService permissionService;
    private final IMessageProviderService messageProviderService;
    private final JailService handler;
    private List<String> allowedCommands;

    @Inject
    public JailListener(final INucleusServiceCollection serviceCollection) {
        this.permissionService = serviceCollection.permissionService();
        this.messageProviderService = serviceCollection.messageProvider();
        this.handler = serviceCollection.getServiceUnchecked(JailService.class);
    }

    // fires after spawn login event
    @Listener
    public void onPlayerLogin(final NucleusOnLoginEvent event, @Getter("getTargetUser") final User user, @Getter("getUserService") final IUserDataObject qs) {
        final Jailing jailing = this.handler.onPlayerLogin(user.getUniqueId());
        if (jailing == JailService.NOT_JAILED) {
            return;
        }

        final Optional<Jail> jail = this.handler.getJail(jailing.getJailName()).filter(x -> x.getLocation().isPresent());
        if (!jail.isPresent()) {
            new PermissionMessageChannel(this.permissionService, JailPermissions.JAIL_NOTIFY)
                    .sendMessage(Component.text("WARNING: No jail is defined for " + user.getName() + " - they're going free!", NamedTextColor.RED));
            this.handler.unjailPlayer(user.getUniqueId());
            return;
        }

        // always send the player back to the jail location
        event.setTo(jail.get().getLocation().get());
    }

    /**
     * At the time the subject joins, check to see if the subject is jailed.
     *
     * @param event The event.
     */
    @Listener(order = Order.LATE)
    public void onPlayerJoin(final ServerSideConnectionEvent.Join event, @Getter("getPlayer") final ServerPlayer serverPlayer) {
        final Optional<Jailing> jailing = this.handler.getPlayerJailData(serverPlayer.getUniqueId());
        if (!jailing.filter(x -> x instanceof JailingEntry).isPresent()) {
            return;
        }

        final JailingEntry entry = jailing.map(x -> (JailingEntry) x).get();
        this.handler.onJail(entry, serverPlayer);
    }

    @Listener
    public void onRequestSent(final NucleusTeleportEvent.Request event, @Root final ServerPlayer cause, @Getter("getPlayer") final ServerPlayer player) {
        if (this.handler.isPlayerJailed(cause.getUniqueId())) {
            event.setCancelled(true);
            event.setCancelMessage(this.messageProviderService.getMessageFor(cause.getLocale(), "jail.teleportcause.isjailed"));
        } else if (this.handler.isPlayerJailed(player.getUniqueId())) {
            event.setCancelled(true);
            event.setCancelMessage(this.messageProviderService.getMessageFor(cause.getLocale(),"jail.teleporttarget.isjailed", player.getName()));
        }
    }

    @Listener
    public void onAboutToTeleport(
            final NucleusTeleportEvent.AboutToTeleport event, @Root final ServerPlayer cause, @Getter("getPlayer") final ServerPlayer player) {
        if (event.getCause().getContext().get(EventContexts.IS_JAILING_ACTION).orElse(false)) {
            if (this.handler.isPlayerJailed(player.getUniqueId())) {
                if (!this.permissionService.hasPermission(cause, JailPermissions.JAIL_TELEPORTJAILED)) {
                    event.setCancelled(true);
                    event.setCancelMessage(
                            this.messageProviderService.getMessageFor(cause, "jail.abouttoteleporttarget.isjailed", player.getName()));
                } else if (!this.permissionService.hasPermission(cause, JailPermissions.JAIL_TELEPORTTOJAILED)) {
                    event.setCancelled(true);
                    event.setCancelMessage(
                            this.messageProviderService.getMessageFor(cause,"jail.abouttoteleportcause.targetisjailed", player.getName()));
                }
            }
        }
    }

    @Listener
    public void onCommand(final ExecuteCommandEvent.Pre event, @Root final ServerPlayer player) {
        // Only if the command is not in the control list.
        if (this.handler.isPlayerJailed(player.getUniqueId()) && this.allowedCommands.stream().noneMatch(x -> event.getCommand().equalsIgnoreCase(x))) {
            event.setCancelled(true);

            // This is the easiest way to send the messages.
            this.handler.notify(player);
        }
    }

    @Listener
    public void onBlockChange(final ChangeBlockEvent event, @Root final ServerPlayer player) {
        if (this.handler.isPlayerJailed(player.getUniqueId())) {
            event.setCancelled(true);
            this.handler.notify(player);
        }
    }

    @Listener
    public void onInteract(final InteractEvent event, @Root final ServerPlayer player) {
        if (this.handler.isPlayerJailed(player.getUniqueId())) {
            event.setCancelled(true);
            this.handler.notify(player);
        }
    }

    @Listener
    public void onSpawn(final RespawnPlayerEvent event, @Root final ServerPlayer player) {
        this.handler.getPlayerJail(player.getUniqueId()).flatMap(NamedLocation::getLocation).ifPresent(event::setToLocation);
    }

    @Listener
    public void onLogout(final ServerSideConnectionEvent.Disconnect event, @Getter("getPlayer") final ServerPlayer serverPlayer) {
        this.handler.clearCacheFor(serverPlayer.getUniqueId());
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.allowedCommands = serviceCollection.configProvider().getModuleConfig(JailConfig.class).getAllowedCommands();
    }
}
