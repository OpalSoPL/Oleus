/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.runnables;

import io.github.nucleuspowered.nucleus.modules.teleport.services.PlayerTeleporterService;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.google.inject.Inject;

public class TeleportAsyncTask implements TaskBase {

    private final PlayerTeleporterService teleporterService;

    @Inject
    public TeleportAsyncTask(final INucleusServiceCollection serviceCollection) {
        this.teleporterService = serviceCollection.getServiceUnchecked(PlayerTeleporterService.class);
    }

    @Override
    public void run() {
        this.teleporterService.removeExpired();
    }

    @Override
    public Duration interval() {
        return Duration.of(2, ChronoUnit.SECONDS);
    }
}
