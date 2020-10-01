/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.back;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.back.commands.BackCommand;
import io.github.nucleuspowered.nucleus.modules.back.commands.ClearBackCommand;
import io.github.nucleuspowered.nucleus.modules.back.config.BackConfig;
import io.github.nucleuspowered.nucleus.modules.back.listeners.BackListeners;
import io.github.nucleuspowered.nucleus.modules.back.services.BackHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BackModule implements IModule.Configurable<BackConfig> {

    public static final String ID = "back";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(BackHandler.class, new BackHandler(), false);
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        final List<Class<? extends ICommandExecutor>> commands = new ArrayList<>();
        commands.add(BackCommand.class);
        commands.add(ClearBackCommand.class);
        return Collections.unmodifiableCollection(commands);
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(BackPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(BackListeners.class);
    }

    @Override
    public Collection<Class<? extends TaskBase>> getTasks() {
        return Collections.emptyList();
    }

    @Override
    public Class<BackConfig> getConfigClass() {
        return BackConfig.class;
    }
}
