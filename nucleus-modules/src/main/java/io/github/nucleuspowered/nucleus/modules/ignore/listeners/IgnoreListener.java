/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ignore.listeners;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.mail.event.NucleusSendMailEvent;
import io.github.nucleuspowered.nucleus.api.module.message.event.NucleusMessageEvent;
import io.github.nucleuspowered.nucleus.modules.ignore.IgnorePermissions;
import io.github.nucleuspowered.nucleus.modules.ignore.services.IgnoreService;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IChatMessageFormatterService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.PlayerChatEvent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.inject.Inject;

public class IgnoreListener implements ListenerBase {

    private final IgnoreService service;
    private final IPermissionService permissionService;
    private final IChatMessageFormatterService chatMessageFormatterService;

    @Inject
    public IgnoreListener(final INucleusServiceCollection serviceCollection) {
        this.service = serviceCollection.getServiceUnchecked(IgnoreService.class);
        this.permissionService = serviceCollection.permissionService();
        this.chatMessageFormatterService = serviceCollection.chatMessageFormatter();
    }

    @Listener(order = Order.LAST)
    private void onChat(final PlayerChatEvent event, @Root final ServerPlayer player) {
        // Reset the channel - but only if we have to.
        if (!this.chatMessageFormatterService.getNucleusChannel(player.getUniqueId())
                .map(IChatMessageFormatterService.Channel::ignoreIgnoreList)
                .orElse(false)) {
            // TODO: Chat Router doesn't tell me who is going to receive the message, so I cannot filter it. API change?
            this.checkCancels(event.getChatRouter().orElseGet(event::getOriginalChatRouter).getMembers(), player).ifPresent(x -> {
                final MutableMessageChannel mmc = event.getChannel().orElseGet(event::getOriginalChannel).asMutable();
                x.forEach(mmc::removeMember);
                event.setChannel(mmc);
            });
        }
    }

    @Listener(order = Order.FIRST)
    public void onMessage(final NucleusMessageEvent event, @Root final ServerPlayer player) {
        if (event.getRecipient().isPresent()) {
            try {
                event.setCancelled(this.service.isIgnored(event.getRecipient().get(), player.getUniqueId()));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void onMail(final NucleusSendMailEvent event, @Root final ServerPlayer player) {
        try {
            event.setCancelled(this.service.isIgnored(event.getRecipient(), player.getUniqueId()));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if we need to cancel messages to people.
     *
     * @param collection The collection to check through.
     * @param player The subject who is sending the message.
     * @return {@link Optional} if unchanged, otherwise a {@link Collection} of {@link Audience}s to remove
     */
    private Optional<Collection<Audience>> checkCancels(final Collection<Audience> collection, final ServerPlayer player) {
        if (this.permissionService.hasPermission(player, IgnorePermissions.IGNORE_CHAT)) {
            return Optional.empty();
        }

        final List<Audience> list = Lists.newArrayList(collection);
        list.removeIf(x -> {
            try {
                if (!(x instanceof ServerPlayer)) {
                    // Remove if not a player.
                    return true;
                }

                if (x.equals(player)) {
                    // Remove if the same player.
                    return true;
                }

                // Don't remove if they are in the list.
                return !this.service.isIgnored(((Player) x).getUniqueId(), player.getUniqueId());
            } catch (final Exception e) {
                e.printStackTrace();

                // Remove them.
                return true;
            }
        });

        // We do this so we don't have to recreate a channel if nothing changes.
        if (list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list);
    }
}
