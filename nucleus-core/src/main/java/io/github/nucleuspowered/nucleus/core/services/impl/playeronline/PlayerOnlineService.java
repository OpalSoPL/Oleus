/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.playeronline;

import io.github.nucleuspowered.nucleus.core.services.interfaces.IPlayerOnlineService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.User;

import java.time.Instant;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import com.google.inject.Singleton;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@Singleton
public class PlayerOnlineService implements IPlayerOnlineService {

    private static final BiPredicate<ServerPlayer, User> STANDARD_ONLINE =
            (source, user) -> Sponge.server().getPlayer(user.getUniqueId()).isPresent();
    private static final BiFunction<@Nullable ServerPlayer, User, Optional<Instant>> STANDARD_LAST_PLAYED =
            (source, user) -> user.get(Keys.LAST_DATE_PLAYED);

    private BiPredicate<ServerPlayer, User> online = STANDARD_ONLINE;
    private BiFunction<ServerPlayer, User, Optional<Instant>> lastPlayed = STANDARD_LAST_PLAYED;

    @Override public boolean isOnline(final ServerPlayer src, final User player) {
        return this.online.test(src, player);
    }

    @Override public Optional<Instant> lastSeen(@Nullable final ServerPlayer src, final User player) {
        return this.lastPlayed.apply(src, player);
    }

    @Override public void set(final BiPredicate<@Nullable ServerPlayer, User> isOnline, final BiFunction<ServerPlayer, User, Optional<Instant>> lastSeen) {
        this.online = isOnline == null ? STANDARD_ONLINE : isOnline;
        this.lastPlayed = lastSeen == null ? STANDARD_LAST_PLAYED : lastSeen;
    }

    @Override public void reset() {
        this.set(null, null);
    }

}
