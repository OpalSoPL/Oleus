/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fun;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.fun.commands.HatCommand;
import io.github.nucleuspowered.nucleus.modules.fun.commands.IgniteCommand;
import io.github.nucleuspowered.nucleus.modules.fun.commands.KittyCannonCommand;
import io.github.nucleuspowered.nucleus.modules.fun.commands.LightningCommand;
import io.github.nucleuspowered.nucleus.modules.fun.commands.RocketCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class FunModule implements IModule {

    public static final String ID = "fun";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                HatCommand.class,
                IgniteCommand.class,
                KittyCannonCommand.class,
                LightningCommand.class,
                RocketCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(FunPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Class<? extends TaskBase>> getTasks() {
        return Collections.emptyList();
    }
}
