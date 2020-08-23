/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandlogger.runnables;

import io.github.nucleuspowered.nucleus.modules.commandlogger.config.CommandLoggerConfig;
import io.github.nucleuspowered.nucleus.modules.commandlogger.services.CommandLoggerHandler;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.google.inject.Inject;

public class CommandLoggerRunnable implements TaskBase, IReloadableService.Reloadable {

    private final CommandLoggerHandler handler;
    private CommandLoggerConfig config;

    @Inject
    public CommandLoggerRunnable(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(CommandLoggerHandler.class);
        this.config = serviceCollection.moduleDataProvider().getModuleConfig(CommandLoggerConfig.class);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public Duration interval() {
        return Duration.of(1, ChronoUnit.SECONDS);
    }

    @Override
    public void accept(final Task task) {
        if (Sponge.getGame().getState() == GameState.SERVER_STOPPED) {
            return;
        }

        if (this.config.isLogToFile()) {
            this.handler.onTick();
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.config = serviceCollection.moduleDataProvider().getModuleConfig(CommandLoggerConfig.class);
    }
}
