/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.mob.config.MobConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Collection;
import java.util.Optional;

public class MobModule implements IModule.Configurable<MobConfig> {

    public final static String ID = "mob";

    @Override public void init(final INucleusServiceCollection serviceCollection) {
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return null;
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.empty();
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return null;
    }

    @Override public Class<MobConfig> getConfigClass() {
        return null;
    }
}
