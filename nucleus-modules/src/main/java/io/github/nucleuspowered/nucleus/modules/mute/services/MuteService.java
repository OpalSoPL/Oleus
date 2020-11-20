/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.services;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.EventContexts;
import io.github.nucleuspowered.nucleus.api.module.jail.NucleusJailService;
import io.github.nucleuspowered.nucleus.api.module.jail.data.Jail;
import io.github.nucleuspowered.nucleus.api.module.jail.data.Jailing;
import io.github.nucleuspowered.nucleus.api.module.mute.NucleusMuteService;
import io.github.nucleuspowered.nucleus.api.module.mute.data.Mute;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanners;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import io.github.nucleuspowered.nucleus.modules.jail.JailKeys;
import io.github.nucleuspowered.nucleus.modules.jail.config.JailConfig;
import io.github.nucleuspowered.nucleus.modules.jail.events.JailEvent;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailLocationEntry;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailingEntry;
import io.github.nucleuspowered.nucleus.modules.mute.MuteKeys;
import io.github.nucleuspowered.nucleus.modules.mute.config.MuteConfig;
import io.github.nucleuspowered.nucleus.modules.mute.data.MuteData;
import io.github.nucleuspowered.nucleus.modules.mute.events.MuteEvent;
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
import org.spongepowered.api.entity.living.player.Player;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class MuteService implements NucleusMuteService, IReloadableService.DataLocationReloadable, IReloadableService.Reloadable {

    public static final Mute NOT_MUTED = new Mute() {
        @Override public String getReason() {
            return null;
        }

        @Override public Optional<UUID> getMuter() {
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

    private boolean isOnlineOnly = false;

    private boolean globalMuteEnabled = false;
    private final List<UUID> voicedUsers = new ArrayList<>();

    private final INucleusServiceCollection serviceCollection;
    private final LoadingCache<UUID, Mute> mutes;

    public MuteService(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
        this.mutes = Caffeine.newBuilder()
                .build(key -> this.serviceCollection.storageManager().getOrCreateUserOnThread(key).get(MuteKeys.MUTE_DATA)
                        .<Mute>map(data -> MutedEntry.fromMuteData(key, data, this.isOnlineOnly))
                        .orElse(MuteService.NOT_MUTED));
    }

    public void checkExpiry() {
        for (final ServerPlayer uuid : Sponge.getServer().getOnlinePlayers()) {
            final Mute mute = this.mutes.get(uuid.getUniqueId());
            if (mute != null && mute != MuteService.NOT_MUTED && mute.expired()) {
                this.serviceCollection.schedulerService().runOnMainThread(() -> this.unmutePlayer(uuid.getUniqueId()));
            }
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.isOnlineOnly = serviceCollection.configProvider().getModuleConfig(MuteConfig.class).isMuteOnlineOnly();
    }

    @Override
    public boolean isMuted(final UUID uuid) {
        final Mute mute = this.mutes.get(uuid);
        return mute != null && mute != MuteService.NOT_MUTED;
    }

    @Override
    public Optional<Mute> getPlayerMuteInfo(final UUID user) {
        final Mute mute = this.mutes.get(user);
        if (mute == null || mute == MuteService.NOT_MUTED) {
            return Optional.empty();
        }
        return Optional.of(mute);
    }

    @Override
    public boolean mutePlayer(final UUID user, final String reason, @Nullable final Duration duration) {
        if (this.isMuted(user)) {
            return false; // already muted
        }

        final Object root = Sponge.getServer().getCauseStackManager().getCurrentCause().root();
        @Nullable final UUID uuid;
        if (root instanceof ServerPlayer) {
            uuid = ((ServerPlayer) root).getUniqueId();
        } else {
            uuid = null;
        }
        final MutedEntry entry = MutedEntry.fromMutingRequest(user, reason, uuid, Instant.now(), duration);
        this.serviceCollection.storageManager().getUserService().setAndSave(user, MuteKeys.MUTE_DATA, entry.asMuteData(this.isOnlineOnly));
        Sponge.getEventManager().post(new MuteEvent.Muted(
                Sponge.getServer().getCauseStackManager().getCurrentCause(),
                user,
                duration,
                Component.text(reason)
        ));

        Sponge.getServer().getPlayer(user).ifPresent(x -> {
            this.mutes.invalidate(user);
            this.mutes.put(user, entry);
            this.onMute(entry, x);
        });
        return true;
    }

    @Override
    public boolean unmutePlayer(final UUID uuid) {
        final Optional<Mute> mute = this.getPlayerMuteInfo(uuid);
        if (mute.isPresent()) {
            this.serviceCollection.storageManager().getUserService().removeAndSave(uuid, MuteKeys.MUTE_DATA);
            this.mutes.invalidate(uuid);
            Sponge.getEventManager().post(new MuteEvent.Unmuted(
                    Sponge.getServer().getCauseStackManager().getCurrentCause(),
                    uuid,
                    mute.get().expired()));

            Sponge.getServer().getPlayer(uuid).ifPresent(x -> {
                this.mutes.put(uuid, MuteService.NOT_MUTED);
                this.serviceCollection.messageProvider().sendMessageTo(x, "mute.elapsed");
            });
            return true;
        }
        return false;
    }

    public void clearCacheFor(final UUID player) {
        final Mute muteData = this.mutes.get(player);
        if (muteData instanceof MutedEntry) {
            this.serviceCollection.storageManager().getUserService()
                    .setAndSave(player, MuteKeys.MUTE_DATA, ((MutedEntry) muteData).asMuteData(this.isOnlineOnly));
        }
        this.mutes.invalidate(player);
    }

    public void onPlayerLogin(final ServerPlayer player) {
        this.mutes.refresh(player.getUniqueId());
        final Mute mute = this.mutes.get(player.getUniqueId());
        if (mute instanceof MutedEntry) {
            this.onMute((MutedEntry) mute, player);
        }
    }


    public boolean isGlobalMuteEnabled() {
        return this.globalMuteEnabled;
    }

    public void setGlobalMuteEnabled(final boolean globalMuteEnabled) {
        if (this.globalMuteEnabled != globalMuteEnabled) {
            this.voicedUsers.clear();
        }

        this.globalMuteEnabled = globalMuteEnabled;
    }

    public boolean isVoiced(final UUID uuid) {
        return this.voicedUsers.contains(uuid);
    }

    public void addVoice(final UUID uuid) {
        this.voicedUsers.add(uuid);
    }

    public void removeVoice(final UUID uuid) {
        this.voicedUsers.remove(uuid);
    }

    @Override
    public void onDataFileLocationChange(final INucleusServiceCollection serviceCollection) {
        this.mutes.invalidateAll();
    }

    public void onMute(final MutedEntry md, final ServerPlayer user) {
        final IMessageProviderService messageProviderService = this.serviceCollection.messageProvider();
        if (md.getRemainingTime().isPresent()) {
            messageProviderService.sendMessageTo(user, "mute.playernotify.time",
                    messageProviderService.getTimeString(user.getLocale(), md.getRemainingTime().get().getSeconds()));
        } else {
            messageProviderService.sendMessageTo(user, "mute.playernotify.standard");
        }
    }


}
