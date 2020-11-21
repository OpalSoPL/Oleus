/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.playeronline.PlayerOnlineService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.time.Instant;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@ImplementedBy(PlayerOnlineService.class)
public interface IPlayerOnlineService {

    boolean isOnline(ServerPlayer src, User player);

    default Optional<Instant> lastSeen(@Nullable final ServerPlayer src, final User player) {
        return player.get(Keys.LAST_DATE_PLAYED);
    }

    void set(@Nullable BiPredicate<ServerPlayer, User> isOnline, @Nullable BiFunction<ServerPlayer, User, Optional<Instant>> lastSeen);

    void reset();

}
