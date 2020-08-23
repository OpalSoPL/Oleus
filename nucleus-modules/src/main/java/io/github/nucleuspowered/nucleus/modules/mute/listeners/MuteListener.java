/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.listeners;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.message.event.NucleusMessageEvent;
import io.github.nucleuspowered.nucleus.datatypes.EndTimestamp;
import io.github.nucleuspowered.nucleus.modules.message.events.InternalNucleusHelpOpEvent;
import io.github.nucleuspowered.nucleus.modules.mute.MutePermissions;
import io.github.nucleuspowered.nucleus.modules.mute.config.MuteConfig;
import io.github.nucleuspowered.nucleus.modules.mute.data.MuteData;
import io.github.nucleuspowered.nucleus.modules.mute.services.MuteHandler;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

public class MuteListener implements IReloadableService.Reloadable, ListenerBase {

    private final MuteHandler handler;
    private final IMessageProviderService messageProvider;
    private final IPermissionService permissionService;
    private MuteConfig muteConfig = new MuteConfig();
    private final PluginContainer pluginContainer;

    @Inject
    public MuteListener(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(MuteHandler.class);
        this.messageProvider = serviceCollection.messageProvider();
        this.permissionService = serviceCollection.permissionService();
        this.pluginContainer = serviceCollection.pluginContainer();
    }

    /**
     * At the time the subject joins, check to see if the subject is muted.
     *
     * @param event The event.
     */
    @Listener
    public void onPlayerLogin(final ClientConnectionEvent.Join event) {
        // Kick off a scheduled task.
        Sponge.getScheduler().createTaskBuilder().async().delay(500, TimeUnit.MILLISECONDS).execute(() -> {
            final Player user = event.getTargetEntity();
            final Optional<MuteData> omd = this.handler.getPlayerMuteData(user);
            if (omd.isPresent()) {
                final MuteData md = omd.get();
                md.nextLoginToTimestamp();

                if (isMuted(user)) {
                    this.handler.onMute(md, event.getTargetEntity());
                }
            }
        }).submit(this.pluginContainer);
    }

    @Listener(order = Order.LATE)
    public void onChat(final MessageChannelEvent.Chat event) {
        Util.onPlayerSimulatedOrPlayer(event, this::onChat);
    }

    private void onChat(final MessageChannelEvent.Chat event, final Player player) {
        boolean cancel = false;
        if (isMuted(player)) {
            this.handler.onMute(player);
            MessageChannel.TO_CONSOLE.send(Text.builder().append(Text.of(player.getName() + " (")).append(
                this.messageProvider.getMessageFor(Sponge.getServer().getConsole(), "standard.muted"))
                    .append(Text.of("): ")).append(event.getRawMessage()).build());
            cancel = true;
        }

        if (cancelOnGlobalMute(player, false)) {
            cancel = true;
        }

        if (cancel) {
            if (this.muteConfig.isShowMutedChat()) {
                // Send it to admins only.
                final String m = this.muteConfig.getCancelledTag();
                if (!m.isEmpty()) {
                    event.getFormatter().setHeader(
                        Text.join(TextSerializers.FORMATTING_CODE.deserialize(m), event.getFormatter().getHeader().toText()));
                }

                this.permissionService.permissionMessageChannel(MutePermissions.MUTE_SEEMUTEDCHAT)
                    .send(player, event.getMessage(), ChatTypes.SYSTEM);
            }

            event.setCancelled(true);
        }
    }

    @Listener
    public void onPlayerMessage(final NucleusMessageEvent event, @Getter("getSender") final Player source) {
        boolean isCancelled = false;
        if (isMuted(source)) {
            if (source.isOnline()) {
                this.handler.onMute(source);
            }

            isCancelled = true;
        }

        if (cancelOnGlobalMute(source, isCancelled)) {
            isCancelled = true;
        }
        event.setCancelled(isCancelled);
    }

    @Listener
    public void onPlayerHelpOp(final InternalNucleusHelpOpEvent event, @Root final Player source) {
        if (isMuted(source)) {
            if (source.isOnline()) {
                this.handler.onMute(source);
            }

            event.setCancelled(true);
        }

        //noinspection IsCancelled
        if (cancelOnGlobalMute(source, event.isCancelled())) {
            event.setCancelled(true);
        }
    }

    private boolean cancelOnGlobalMute(final Player player, final boolean isCancelled) {
        if (isCancelled || !this.handler.isGlobalMuteEnabled() || this.permissionService.hasPermission(player, MutePermissions.VOICE_AUTO)) {
            return false;
        }

        if (this.handler.isVoiced(player.getUniqueId())) {
            return false;
        }

        this.messageProvider.sendMessageTo(player, "globalmute.novoice");
        return true;
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.muteConfig = serviceCollection.moduleDataProvider().getModuleConfig(MuteConfig.class);
    }

    private boolean isMuted(final Player player) {
        if (!this.handler.isMutedCached(player)) {
            return false;
        } else if (this.handler.getPlayerMuteData(player).map(EndTimestamp::expired).orElse(true)) { // true indicates expiry
            this.handler.unmutePlayer(player);
            return false;
        }

        return true;
    }
}
