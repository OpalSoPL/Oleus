/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.listeners;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.api.core.event.NucleusFirstJoinEvent;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.core.CoreKeys;
import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.core.events.NucleusOnLoginEvent;
import io.github.nucleuspowered.nucleus.core.events.OnFirstLoginEvent;
import io.github.nucleuspowered.nucleus.core.events.UserDataLoadedEvent;
import io.github.nucleuspowered.nucleus.core.services.UniqueUserService;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.util.AdventureUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationService;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;
import com.google.inject.Inject;

public class CoreListener implements IReloadableService.Reloadable, ListenerBase {

    private final INucleusServiceCollection serviceCollection;
    @Nullable private NucleusTextTemplate getKickOnStopMessage = null;
    @Nullable private final URL url;
    private boolean warnOnWildcard = true;

    @Inject
    public CoreListener(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
        URL u = null;
        try {
            u = new URL("https://ore.spongepowered.org/Nucleus/Nucleus/pages/The-Permissions-Wildcard-(And-Why-You-Shouldn't-Use-It)");
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        this.url = u;
    }

    @Listener(order = Order.POST)
    public void onPlayerAuth(final ServerSideConnectionEvent.Auth event) {
        final UUID userId = event.getProfile().getUniqueId();
        if (userId == null) { // it could be, I guess?
            return;
        }

        // Create user data if required, and place into cache.
        // As this is already async, load on thread.
        final IUserDataObject dataObject = this.serviceCollection.storageManager().getUserService().getOrNewOnThread(userId);

        // Fire the event, which will be async too, perhaps unsurprisingly.
        // The main use for this will be migrations.
        final UserDataLoadedEvent eventToFire = new UserDataLoadedEvent(
                event.getCause().with(this.serviceCollection.pluginContainer()),
                dataObject,
                event.getProfile()
        );
        Sponge.getEventManager().post(eventToFire);
        if (eventToFire.shouldSave()) {
            this.serviceCollection.storageManager().getUserService().save(userId, dataObject);
        }
        this.serviceCollection.messageProvider().invalidateLocaleCacheFor(userId);
    }

    /* (non-Javadoc)
     * We do this last to avoid interfering with other modules.
     */
    @Listener(order = Order.LATE)
    public void onPlayerLoginLast(final ServerSideConnectionEvent.Login event, @Getter("getProfile") final GameProfile profile,
        @Getter("getUser") final User user) {

        final IUserDataObject udo = this.serviceCollection.storageManager().getUserService().getOrNewOnThread(user.getUniqueId());

        if (event.getFromLocation().equals(event.getToLocation())) {
            try (final CauseStackManager.StackFrame frame = Sponge.getServer().getCauseStackManager().pushCauseFrame()) {
                frame.pushCause(profile);
                // Check this
                final NucleusOnLoginEvent onLoginEvent = new NucleusOnLoginEvent(frame.getCurrentCause(), user, udo, event.getFromLocation());

                Sponge.getEventManager().post(onLoginEvent);
                if (onLoginEvent.getTo().isPresent()) {
                    event.setToLocation(onLoginEvent.getTo().get());
                }
            }
        }

        this.serviceCollection.userCacheService().updateCacheForPlayer(user.getUniqueId(), udo);
    }

    /* (non-Javadoc)
     * We do this first to try to get the first play status as quick as possible.
     */
    @Listener(order = Order.FIRST)
    public void onPlayerJoinFirst(final ServerSideConnectionEvent.Join event, @Getter("getPlayer") final ServerPlayer player) {
        try {
            final IUserDataObject qsu = this.serviceCollection.storageManager().getUserService().getOrNewOnThread(player.getUniqueId());
            qsu.set(CoreKeys.LAST_LOGIN, Instant.now());
            if (this.serviceCollection.platformService().isServer()) {
                qsu.set(CoreKeys.IP_ADDRESS, player.getConnection().getAddress().getAddress().toString());
            }

            // We'll do this bit shortly - after the login events have resolved.
            final String name = player.getName();
            Task.builder().execute(() -> qsu.set(CoreKeys.LAST_KNOWN_NAME, name)).delayTicks(20L).plugin(this.serviceCollection.pluginContainer()).build();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onPlayerJoinLast(final ServerSideConnectionEvent.Join event, @Getter("getPlayer") final ServerPlayer player) {
        // created before
        // TODO: Fix this
        if (!this.serviceCollection.storageManager().getUserService().getOnThread(player.getUniqueId())
                .map(x -> x.get(CoreKeys.FIRST_JOIN)).isPresent()) {
            this.serviceCollection.getServiceUnchecked(UniqueUserService.class).resetUniqueUserCount();

            final NucleusFirstJoinEvent firstJoinEvent = new OnFirstLoginEvent(
                event.getCause(), player, event.getOriginalAudience(),
                    event.getAudience().orElseGet(Sponge::getSystemSubject),
                    event.getOriginalMessage(),
                    event.isMessageCancelled());

            Sponge.getEventManager().post(firstJoinEvent);
            firstJoinEvent.getAudience().ifPresent(event::setAudience);
            event.setMessageCancelled(firstJoinEvent.isMessageCancelled());
            this.serviceCollection.storageManager().getUserService()
                    .getOrNew(player.getUniqueId())
                    .thenAccept(x -> x.set(CoreKeys.FIRST_JOIN, x.get(CoreKeys.LAST_LOGIN).orElseGet(Instant::now)));
        }

        // Warn about wildcard.
        if (player.hasPermission("nucleus")) {
            final IMessageProviderService provider = this.serviceCollection.messageProvider();
            this.serviceCollection.logger().warn("The player " + player.getName() + " has got either the nucleus wildcard or the * wildcard "
                    + "permission. This may cause unintended side effects.");

            if (this.warnOnWildcard) {
                // warn
                final List<Component> text = new ArrayList<>();
                text.add(provider.getMessageFor(player, "core.permission.wildcard2"));
                text.add(provider.getMessageFor(player, "core.permission.wildcard3"));
                if (this.url != null) {
                    text.add(
                            Component.text().append(provider.getMessageFor(player, "core.permission.wildcard4"))
                                    .clickEvent(ClickEvent.openUrl(this.url)).build()
                    );
                }
                text.add(provider.getMessageFor(player, "core.permission.wildcard5"));
                Sponge.getServiceProvider().paginationService()
                        .builder()
                        .title(provider.getMessageFor(player, "core.permission.wildcard"))
                        .contents(text)
                        .padding(Component.text("-", NamedTextColor.GOLD))
                        .sendTo(player);
            }
        }
    }

    @Listener(order = Order.LAST)
    public void onPlayerQuit(final ServerSideConnectionEvent.Disconnect event, @Getter("getPlayer") final ServerPlayer player) {
        this.serviceCollection.storageManager().getUser(player.getUniqueId()).thenAccept(x -> x.ifPresent(y -> this.onPlayerQuit(player, y)));
    }

    private void onPlayerQuit(final ServerPlayer player, final IUserDataObject udo) {
        final InetAddress address = player.getConnection().getAddress().getAddress();

        try {
            udo.set(CoreKeys.IP_ADDRESS, address.toString());
            this.serviceCollection.userCacheService().updateCacheForPlayer(player.getUniqueId(), udo);
            this.serviceCollection.storageManager().saveUser(player.getUniqueId(), udo);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        final CoreConfig c = this.serviceCollection.configProvider().getModuleConfig(CoreConfig.class);
        this.getKickOnStopMessage = c.isKickOnStop() ? c.getKickOnStopMessage() : null;
        this.warnOnWildcard = c.isCheckForWildcard();
    }

    @Listener
    public void onServerAboutToStop(final StoppingEngineEvent<Server> event) {
        for (final ServerPlayer player : Sponge.getServer().getOnlinePlayers()) {
            this.serviceCollection.storageManager().getUserOnThread(player.getUniqueId()).ifPresent(x -> this.onPlayerQuit(player, x));
        }

        if (this.getKickOnStopMessage != null) {
            for (final ServerPlayer p : Sponge.getServer().getOnlinePlayers()) {
                final Component msg = this.getKickOnStopMessage.getForObject(p);
                if (AdventureUtils.isEmpty(msg)) {
                    p.kick();
                } else {
                    p.kick(msg);
                }
            }
        }

    }

    @Listener
    public void onGameReload(final RefreshGameEvent event) {
        final Audience requester = event.getCause().first(Audience.class).orElse(Sponge.getSystemSubject());
        final IMessageProviderService messageProviderService = this.serviceCollection.messageProvider();
        try {
            this.serviceCollection.reloadableService().fireReloadables(this.serviceCollection);
            requester.sendMessage(Component.text().content("[Nucleus] ")
                            .color(NamedTextColor.YELLOW)
                            .append(messageProviderService.getMessageFor(requester, "command.reload.one")).build());
            requester.sendMessage(Component.text().content("[Nucleus] ")
                    .color(NamedTextColor.YELLOW)
                    .append(messageProviderService.getMessageFor(requester, "command.reload.two")).build());
        } catch (final Exception e) {
            e.printStackTrace();
            requester.sendMessage(
                    Component.text().content("[Nucleus] ")
                            .color(NamedTextColor.RED)
                            .append(messageProviderService.getMessageFor(requester, "command.reload.errorone")).build());
        }
    }
}
