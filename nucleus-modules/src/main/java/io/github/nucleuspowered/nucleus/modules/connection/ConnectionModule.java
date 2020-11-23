/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.connection;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.connection.config.ConnectionConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.modules.connection.listeners.ConnectionListener;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ConnectionModule implements IModule.Configurable<ConnectionConfig> {

    public static final String ID = "connection";

    @Override public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.emptyList();
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(ConnectionPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(ConnectionListener.class);
    }

    @Override public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.emptyList();
    }

    @Override public Class<ConnectionConfig> getConfigClass() {
        return ConnectionConfig.class;
    }
}
