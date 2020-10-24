/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandlogger;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.commandlogger.config.CommandLoggerConfig;
import io.github.nucleuspowered.nucleus.modules.commandlogger.listeners.CommandLoggingListener;
import io.github.nucleuspowered.nucleus.modules.commandlogger.runnables.CommandLoggerRunnable;
import io.github.nucleuspowered.nucleus.modules.commandlogger.services.CommandLoggerHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class CommandLoggerModule implements IModule.Configurable<CommandLoggerConfig> {

    public static final String ID = "command-logger";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(CommandLoggerHandler.class, new CommandLoggerHandler(serviceCollection), false);
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.empty();
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(CommandLoggingListener.class);
    }

    @Override
    public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.singleton(CommandLoggerRunnable.class);
    }

    @Override
    public Class<CommandLoggerConfig> getConfigClass() {
        return CommandLoggerConfig.class;
    }
}
