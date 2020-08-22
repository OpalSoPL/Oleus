/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.module.freezeplayer;

import java.util.UUID;

/**
 * A service that controls whether a player is frozen.
 *
 * <p>Requires the <strong>freeze-player</strong> module.</p>
 */
public interface NucleusFreezePlayerService {

    /**
     * Gets whether a player is frozen.
     *
     * @param uuid The {@link UUID} of the player.
     * @return Whether they are frozen
     */
    boolean isFrozen(UUID uuid);

    /**
     * Sets whether a player is frozen.
     *
     * @param uuid The {@link UUID} of the player
     * @param freeze Whether to freeze them
     */
    void setFrozen(UUID uuid, boolean freeze);

}
