/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.api.teleport.NucleusSafeLocationService;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportResult;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanner;
import io.github.nucleuspowered.nucleus.core.services.impl.teleport.SafeLocationService;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;
import org.spongepowered.math.vector.Vector3d;

@ImplementedBy(SafeLocationService.class)
public interface INucleusLocationService extends NucleusSafeLocationService {

    TeleportResult teleportPlayerSmart(ServerPlayer player,
            ServerLocation transform,
            Vector3d rotation,
            boolean centreBlock,
            boolean safe,
            TeleportScanner scanner);

    TeleportResult teleportPlayerSmart(ServerPlayer player,
            ServerLocation location,
            boolean centreBlock,
            boolean safe,
            TeleportScanner scanner);

    TeleportHelperFilter getAppropriateFilter(ServerPlayer src, boolean safeTeleport);

    TeleportResult teleportPlayer(ServerPlayer player,
            ServerLocation location,
            Vector3d rotation,
            boolean centreBlock,
            TeleportScanner scanner,
            TeleportHelperFilter filter,
            TeleportHelperFilter... filters);

    BorderDisableSession temporarilyDisableBorder(boolean reset, ServerWorld world);

    interface BorderDisableSession extends AutoCloseable {

        @Override default void close() { }
    }
}
