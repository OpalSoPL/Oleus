/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.runnables;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.mute.services.MuteService;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class MuteTask implements TaskBase {

    private final MuteService muteHandler;

    @Inject
    public MuteTask(final INucleusServiceCollection serviceCollection) {
        this.muteHandler = serviceCollection.getServiceUnchecked(MuteService.class);
    }

    @Override
    public void run() {
        this.muteHandler.checkExpiry();
    }

    @Override
    public Duration interval() {
        return Duration.of(1, ChronoUnit.SECONDS);
    }
}
