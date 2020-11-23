/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.core.services.impl.platform.PlatformService;

import java.time.Instant;
import java.util.Optional;

@ImplementedBy(PlatformService.class)
public interface IPlatformService {

    boolean isServer();

    Optional<Instant> gameStartedTime();

    void setGameStartedTime();

    void unsetGameStartedTime();
}
