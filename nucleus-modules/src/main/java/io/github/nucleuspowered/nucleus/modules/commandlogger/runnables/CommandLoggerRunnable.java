/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandlogger.runnables;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.commandlogger.config.CommandLoggerConfig;
import io.github.nucleuspowered.nucleus.modules.commandlogger.services.CommandLoggerHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class CommandLoggerRunnable implements TaskBase, IReloadableService.Reloadable {

    private final CommandLoggerHandler handler;
    private CommandLoggerConfig config;

    @Inject
    public CommandLoggerRunnable(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(CommandLoggerHandler.class);
        this.config = serviceCollection.configProvider().getModuleConfig(CommandLoggerConfig.class);
    }

    @Override
    public Duration interval() {
        return Duration.of(1, ChronoUnit.SECONDS);
    }

    // TODO: Only run when server is running.
    @Override
    public void run() {
        try {
            Sponge.server();
            if (this.config.isLogToFile()) {
                this.handler.onTick();
            }
        } catch (final IllegalStateException e) {
            // no-op
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.config = serviceCollection.configProvider().getModuleConfig(CommandLoggerConfig.class);
    }
}
