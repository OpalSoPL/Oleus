/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mob;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.mob.commands.SpawnMobCommand;
import io.github.nucleuspowered.nucleus.modules.mob.config.MobConfig;
import io.github.nucleuspowered.nucleus.modules.mob.listeners.BlockLivingSpawnListener;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class MobModule implements IModule.Configurable<MobConfig> {

    public final static String ID = "mob";

    @Override public void init(final INucleusServiceCollection serviceCollection) {
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Collections.singletonList(SpawnMobCommand.class);
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(MobPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(BlockLivingSpawnListener.class);
    }

    @Override public Class<MobConfig> getConfigClass() {
        return MobConfig.class;
    }
}
