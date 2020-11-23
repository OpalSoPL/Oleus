/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.runnables;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailService;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class JailTask implements TaskBase {

    private final JailService jailHandler;

    @Inject
    public JailTask(final INucleusServiceCollection serviceCollection) {
        this.jailHandler = serviceCollection.getServiceUnchecked(JailService.class);
    }

    @Override
    @NonNull
    public Duration interval() {
        return Duration.of(1, ChronoUnit.SECONDS);
    }

    @Override
    public void run() {
        this.jailHandler.checkExpiry();
    }
}
