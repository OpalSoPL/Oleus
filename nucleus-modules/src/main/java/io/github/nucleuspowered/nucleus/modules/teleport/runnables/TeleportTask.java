/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.runnables;

import io.github.nucleuspowered.nucleus.modules.teleport.services.PlayerTeleporterService;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.scheduler.Task;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.google.inject.Inject;

public class TeleportTask implements TaskBase {

    private final PlayerTeleporterService teleporterService;

    @Inject
    public TeleportTask(final INucleusServiceCollection serviceCollection) {
        this.teleporterService = serviceCollection.getServiceUnchecked(PlayerTeleporterService.class);
    }

    @Override
    public void accept(final Task task) {
        teleporterService.removeExpired();
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public Duration interval() {
        return Duration.of(2, ChronoUnit.SECONDS);
    }
}
