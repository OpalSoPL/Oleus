/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.spawn.commands.FirstSpawnCommand;
import io.github.nucleuspowered.nucleus.modules.spawn.commands.RemoveFirstSpawnCommand;
import io.github.nucleuspowered.nucleus.modules.spawn.commands.SetFirstSpawnCommand;
import io.github.nucleuspowered.nucleus.modules.spawn.commands.SetSpawnCommand;
import io.github.nucleuspowered.nucleus.modules.spawn.commands.SpawnCommand;
import io.github.nucleuspowered.nucleus.modules.spawn.commands.SpawnOtherCommand;
import io.github.nucleuspowered.nucleus.modules.spawn.config.SpawnConfig;
import io.github.nucleuspowered.nucleus.modules.spawn.listeners.SpawnListener;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class SpawnModule implements IModule.Configurable<SpawnConfig> {

    public static final String ID = "spawn";

    @Override public void init(final INucleusServiceCollection serviceCollection) {
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                FirstSpawnCommand.class,
                RemoveFirstSpawnCommand.class,
                SetFirstSpawnCommand.class,
                SetSpawnCommand.class,
                SpawnCommand.class,
                SpawnOtherCommand.class
        );
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(SpawnPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(SpawnListener.class);
    }

    @Override public Class<SpawnConfig> getConfigClass() {
        return SpawnConfig.class;
    }
}
