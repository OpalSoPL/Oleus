/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jump;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.jump.commands.JumpCommand;
import io.github.nucleuspowered.nucleus.modules.jump.commands.ThruCommand;
import io.github.nucleuspowered.nucleus.modules.jump.commands.TopCommand;
import io.github.nucleuspowered.nucleus.modules.jump.commands.UnstuckCommand;
import io.github.nucleuspowered.nucleus.modules.jump.config.JumpConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class JumpModule implements IModule.Configurable<JumpConfig> {

    public final static String ID = "jump";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                JumpCommand.class,
                ThruCommand.class,
                TopCommand.class,
                UnstuckCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(JumpPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public Class<JumpConfig> getConfigClass() {
        return JumpConfig.class;
    }
}
