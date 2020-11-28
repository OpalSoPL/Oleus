/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.ignore;

import java.util.Collection;
import java.util.UUID;

/**
 * Gets the users that have been ignored (via Nucleus) for a specific player.
 */
public interface NucleusIgnoreService {

    boolean isIgnored(UUID ignorer, UUID ignoree);

    /**
     * Gets the {@link UUID}s of the players that are ignored by the given
     * player with the given {@link UUID}.
     *
     * @param uuid The {@link UUID} of the player to check.
     * @return The collection of {@link UUID}.
     */
    Collection<UUID> getIgnoredBy(UUID uuid);

}
