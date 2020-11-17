/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.services;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.EventContexts;
import io.github.nucleuspowered.nucleus.api.module.jail.NucleusJailService;
import io.github.nucleuspowered.nucleus.api.module.jail.data.Jail;
import io.github.nucleuspowered.nucleus.api.module.jail.data.Jailing;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanners;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import io.github.nucleuspowered.nucleus.modules.jail.JailKeys;
import io.github.nucleuspowered.nucleus.modules.jail.config.JailConfig;
import io.github.nucleuspowered.nucleus.modules.jail.events.JailEvent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import io.vavr.control.Either;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.teleport.TeleportHelperFilters;
import org.spongepowered.math.vector.Vector3d;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class JailService implements NucleusJailService, IReloadableService.DataLocationReloadable, IReloadableService.Reloadable {

    public static final Jailing NOT_JAILED = new Jailing() {
        @Override public String getReason() {
            return null;
        }

        @Override public String getJailName() {
            return null;
        }

        @Override public Optional<UUID> getJailer() {
            return Optional.empty();
        }

        @Override public Optional<ServerLocation> getPreviousLocation() {
            return Optional.empty();
        }

        @Override public Optional<Instant> getCreationInstant() {
            return Optional.empty();
        }

        @Override public Optional<Duration> getRemainingTime() {
            return Optional.empty();
        }

        @Override public boolean expired() {
            return false;
        }

        @Override public boolean isCurrentlyTicking() {
            return false;
        }
    };

    private boolean isPopulated = false;
    private boolean isOnlineOnly = false;

    private final INucleusServiceCollection serviceCollection;
    private final Map<String, Jail> jails = new HashMap<>();
    private final LoadingCache<UUID, Jailing> jailings;

    public JailService(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
        this.jailings = Caffeine.newBuilder()
                .build(key -> this.serviceCollection.storageManager().getOrCreateUserOnThread(key).get(JailKeys.JAIL_DATA)
                        .<Jailing>map(data -> JailingEntry.fromJailingData(key, data, this.isOnlineOnly))
                        .orElse(JailService.NOT_JAILED));
    }

    @Override
    public Optional<Jail> setJail(final String name, final ServerLocation location, final Vector3d rotation) {
        this.populateJails();
        return Optional.empty();
    }

    @Override
    public Map<String, Jail> getJails() {
        this.populateJails();
        return Collections.unmodifiableMap(this.jails);
    }

    @Override
    public Optional<Jail> getJail(final String name) {
        this.populateJails();
        return Optional.ofNullable(this.jails.get(name));
    }

    @Override
    public boolean removeJail(final String name) {
        this.populateJails();
        return false;
    }

    @Override
    public boolean isPlayerJailed(final UUID user) {
        final Jailing j = this.jailings.get(user);
        return j != null && j != JailService.NOT_JAILED;
    }

    @Override
    public Optional<Jailing> getPlayerJailData(final UUID uuid) {
        final Jailing jailing = this.jailings.get(uuid);
        if (jailing == null || jailing == JailService.NOT_JAILED) {
            return Optional.empty();
        }
        return Optional.of(jailing);
    }

    @Override
    public boolean jailPlayer(final UUID victim, final Jail jail, final String reason, @Nullable final Duration duration) {
        this.populateJails();
        if (this.isPlayerJailed(victim)) {
            return false;
        }

        final ServerLocation location = jail.getLocation().orElseThrow(() -> new IllegalArgumentException("Jail does not have a valid location."));
        if (location.isValid()) {
            this.serviceCollection.schedulerService().runOnMainThread(() -> Sponge.getServer().getWorldManager().loadWorld(location.getWorldKey())).join();
        } else {
            throw new IllegalArgumentException("Jail does not have a valid location.");
        }

        final Cause cause = Sponge.getServer().getCauseStackManager().getCurrentCause();

        // Create the jailing
        final JailingEntry jailingEntry = JailingEntry.fromJailingRequest(
                victim,
                reason,
                jail.getName(),
                cause.first(ServerPlayer.class).map(Identifiable::getUniqueId).orElse(null),
                this.eitherToLocation(this.getUserEither(victim)),
                Instant.now(),
                duration);
        this.jailings.put(victim, jailingEntry);
        this.serviceCollection.storageManager().getUserService().setAndSave(victim, JailKeys.JAIL_DATA, jailingEntry.asJailData(this.isOnlineOnly));
        // Time to jail
        final Optional<ServerPlayer> serverPlayer = Sponge.getServer().getPlayer(victim);
        if (serverPlayer.isPresent()) {
            final ServerPlayer player = serverPlayer.get();
            this.serviceCollection.schedulerService().runOnMainThread(() -> {
                try (final CauseStackManager.StackFrame frame = Sponge.getServer().getCauseStackManager().pushCauseFrame()) {
                    frame.addContext(EventContexts.IS_JAILING_ACTION, true);
                    player.setLocation(location);
                    player.setRotation(jail.getRotation());
                    player.offer(Keys.IS_FLYING, false);
                    player.offer(Keys.CAN_FLY, false);
                    this.onJail(jailingEntry, player);
                }
            });
        }

        Sponge.getEventManager().post(new JailEvent.Jailed(
                victim,
                cause,
                jail.getName(),
                LegacyComponentSerializer.legacySection().deserialize(reason),
                jailingEntry.getRemainingTime().orElse(null)));

        return true;
    }

    @Override
    public boolean unjailPlayer(final UUID user) {
        this.populateJails();
        final Optional<Jailing> o = this.getPlayerJailData(user);
        if (!o.isPresent()) {
            return false;
        }

        final Jailing jailing = o.get();
        final Either<ServerPlayer, User> either = Sponge.getServer().getPlayer(user)
                .<Either<ServerPlayer, User>>map(Either::left)
                .orElseGet(() -> this.getUserEither(user));

        final ServerLocation destination = jailing.getPreviousLocation().orElseGet(() -> either.fold(player -> {
            final WorldProperties worldProperties = player.getWorld().getProperties();
            return ServerLocation.of(worldProperties.getKey(), worldProperties.getSpawnPosition());
        }, u -> {
            final WorldProperties def = Sponge.getServer().getWorldManager().getDefaultProperties().get();
            if (u == null) {
                return ServerLocation.of(def.getKey(), def.getSpawnPosition());
            }
            final WorldProperties target =
                    Sponge.getServer().getWorldManager().getWorld(u.getWorldKey()).map(ServerWorld::getProperties).orElse(def);
            return ServerLocation.of(target.getKey(), target.getSpawnPosition());
        }));

        this.jailings.put(user, JailService.NOT_JAILED);
        this.serviceCollection.storageManager().getUserService().removeAndSave(user, JailKeys.JAIL_DATA);
        this.serviceCollection.schedulerService().runOnMainThread(() -> {
            final ServerLocation serverLocation = this.serviceCollection.teleportService().getSafeLocation(
                    destination,
                    TeleportScanners.NO_SCAN.get(),
                    TeleportHelperFilters.DEFAULT.get()
            ).orElseGet(() -> {
                final WorldProperties def = Sponge.getServer().getWorldManager().getDefaultProperties().get();
                return ServerLocation.of(def.getKey(), def.getSpawnPosition());
            });

            if (either.isLeft()) {
                either.getLeft().setLocation(serverLocation);
                this.serviceCollection.messageProvider().sendMessageTo(either.getLeft(), "jail.elapsed");
            } else if (either.get() != null) {
                either.get().setLocation(serverLocation.getWorldKey(), serverLocation.getPosition());
            }
        });

        // Return player to the specified location
        Sponge.getEventManager().post(new JailEvent.Unjailed(user, Sponge.getServer().getCauseStackManager().getCurrentCause()));
        return true;
    }

    public void clearCacheFor(final UUID player) {
        final Jailing jailData = this.jailings.get(player);
        if (jailData instanceof JailingEntry) {
            this.serviceCollection.storageManager().getUserService().setAndSave(player, JailKeys.JAIL_DATA, ((JailingEntry) jailData).asJailData(this.isOnlineOnly));
        }
        this.jailings.invalidate(player);
    }

    public Jailing onPlayerLogin(final UUID player) {
        this.jailings.refresh(player);
        return this.jailings.get(player);
    }

    @Override
    public void onDataFileLocationChange(final INucleusServiceCollection serviceCollection) {
        this.jails.clear();
        this.jailings.invalidateAll();
        this.isPopulated = false;
    }

    private void populateJails() {
        if (!this.isPopulated) {
            synchronized (this) {
                if (!this.isPopulated) {
                    this.jails.clear();
                    for (final Map.Entry<String, NamedLocation> entry :
                            this.serviceCollection.storageManager().getGeneral().get(JailKeys.JAILS).orElseGet(Collections::emptyMap).entrySet()) {
                        this.jails.put(entry.getKey(), new JailLocationEntry(entry.getValue()));
                    }
                    this.isPopulated = true;
                }
            }
        }
    }

    public void onJail(final JailingEntry entry, final ServerPlayer serverPlayer) {
        final IMessageProviderService messageProviderService = this.serviceCollection.messageProvider();
        final IPlayerDisplayNameService playerDisplayNameService = this.serviceCollection.playerDisplayNameService();
        try (final CauseStackManager.StackFrame frame = Sponge.getServer().getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContexts.IS_JAILING_ACTION, true);
            // It exists.
            final Optional<Duration> timeLeft = entry.getRemainingTime();
            final Component message = timeLeft.map(duration ->
                    messageProviderService.getMessageFor(
                            serverPlayer,
                            "command.jail.jailedfor",
                            entry.getJailName(),
                            playerDisplayNameService.getDisplayName(entry.getJailer().orElse(Util.CONSOLE_FAKE_UUID)),
                            messageProviderService.getTimeString(serverPlayer.getLocale(), duration.getSeconds()))
            )
                    .orElseGet(() -> messageProviderService.getMessageFor(serverPlayer, "command.jail.jailedperm", entry.getJailName(),
                            playerDisplayNameService.getDisplayName(entry.getJailer().orElse(Util.CONSOLE_FAKE_UUID)), "", ""));

            serverPlayer.sendMessage(message);
            messageProviderService.sendMessageTo(serverPlayer, "standard.reasoncoloured", entry.getReason());
        }
    }

    public void notify(final ServerPlayer user) {
        this.getPlayerJailData(user.getUniqueId()).ifPresent(entry -> {
            final IMessageProviderService messageProviderService = this.serviceCollection.messageProvider();
            final Optional<Duration> timeLeft = entry.getRemainingTime();
            if (timeLeft.isPresent()) {
                messageProviderService.sendMessageTo(
                        user, "jail.playernotify.time",
                        messageProviderService.getTimeString(user.getLocale(), timeLeft.get().getSeconds())
                );
            } else {
                messageProviderService.sendMessageTo(user, "jail.playernotify.standard");
            }

            messageProviderService.sendMessageTo(user,"standard.reasoncoloured",
                    LegacyComponentSerializer.legacyAmpersand().deserialize(entry.getReason()));
        });

    }

    private Either<ServerPlayer, User> getUserEither(final UUID uuid) {
        return Sponge.getServer().getUserManager().get(uuid).<Either<ServerPlayer, User>>map(Either::right).orElse(null);
    }

    private ServerLocation eitherToLocation(final Either<ServerPlayer, User> either) {
        if (either.isRight()) {
            return ServerLocation.of(either.get().getWorldKey(), either.get().getPosition());
        } else {
            return either.getLeft().getServerLocation();
        }
    }

    public void checkExpiry() {
        for (final ServerPlayer uuid : Sponge.getServer().getOnlinePlayers()) {
            final Jailing jailing = this.jailings.get(uuid.getUniqueId());
            if (jailing != null && jailing != JailService.NOT_JAILED && jailing.expired()) {
                this.serviceCollection.schedulerService().runOnMainThread(() -> this.unjailPlayer(uuid.getUniqueId()));
            }
        }
    }

    public Optional<Jail> getPlayerJail(final UUID uniqueId) {
        return this.getPlayerJailData(uniqueId).flatMap(x -> this.getJail(x.getJailName()));
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.isOnlineOnly = serviceCollection.configProvider().getModuleConfig(JailConfig.class).isJailOnlineOnly();
    }
}
