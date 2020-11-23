/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.runnables;

import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.google.inject.Inject;

public class AFKTask implements TaskBase {

    private final AFKHandler handler;

    @Inject
    public AFKTask(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(AFKHandler.class);
    }

    @Override
    public void run() {
        this.handler.onTick();
    }

    @Override
    public Duration interval() {
        return Duration.of(1, ChronoUnit.SECONDS);
    }
}
