/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.teleport.data;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;

import java.util.function.Supplier;

public final class TeleportScanners {

    public static final Supplier<TeleportScanner> ASCENDING_SCAN =
            () -> Sponge.getRegistry().getCatalogRegistry().get(TeleportScanner.class, ResourceKey.of("nucleus", "ascending_scan")).get();

    public static final Supplier<TeleportScanner> DESCENDING_SCAN =
            () -> Sponge.getRegistry().getCatalogRegistry().get(TeleportScanner.class, ResourceKey.of("nucleus", "descending_scan")).get();

    public static final Supplier<TeleportScanner> NO_SCAN =
            () -> Sponge.getRegistry().getCatalogRegistry().get(TeleportScanner.class, ResourceKey.of("nucleus", "no_scan")).get();
}
