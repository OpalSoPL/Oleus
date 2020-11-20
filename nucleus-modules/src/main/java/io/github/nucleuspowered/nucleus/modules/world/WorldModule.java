/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.world.config.WorldConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Collection;
import java.util.Optional;

public class WorldModule implements IModule.Configurable<WorldConfig> {

    public static final String ID = "world";

    @Override
    public void init(INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return null;
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.empty();
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return null;
    }

    @Override
    public Class<WorldConfig> getConfigClass() {
        return WorldConfig.class;
    }
}
