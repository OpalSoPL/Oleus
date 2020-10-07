/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.connectionmessages;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.connectionmessages.config.ConnectionMessagesConfig;
import io.github.nucleuspowered.nucleus.modules.connectionmessages.listeners.ConnectionMessagesForceListener;
import io.github.nucleuspowered.nucleus.modules.connectionmessages.listeners.ConnectionMessagesListener;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class ConnectionMessagesModule implements IModule.Configurable<ConnectionMessagesConfig> {

    public static final String ID = "connection-messages";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(ConnectionMessagesPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Arrays.asList(
                ConnectionMessagesForceListener.class,
                ConnectionMessagesListener.class
        );
    }

    @Override
    public Collection<Class<? extends TaskBase>> getTasks() {
        return Collections.emptyList();
    }

    @Override
    public Class<ConnectionMessagesConfig> getConfigClass() {
        return ConnectionMessagesConfig.class;
    }
}
