/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.environment;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.environment.commands.AddTimeCommand;
import io.github.nucleuspowered.nucleus.modules.environment.commands.LockWeatherCommand;
import io.github.nucleuspowered.nucleus.modules.environment.commands.SetTimeCommand;
import io.github.nucleuspowered.nucleus.modules.environment.commands.TimeCommand;
import io.github.nucleuspowered.nucleus.modules.environment.commands.WeatherCommand;
import io.github.nucleuspowered.nucleus.modules.environment.config.EnvironmentConfig;
import io.github.nucleuspowered.nucleus.modules.environment.listeners.EnvironmentListener;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class EnvironmentModule implements IModule.Configurable<EnvironmentConfig> {

    public static final String ID = "environment";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                LockWeatherCommand.class,
                AddTimeCommand.class,
                SetTimeCommand.class,
                TimeCommand.class,
                WeatherCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(EnvironmentPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(EnvironmentListener.class);
    }

    @Override
    public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.emptyList();
    }

    @Override
    public Class<EnvironmentConfig> getConfigClass() {
        return EnvironmentConfig.class;
    }
}
