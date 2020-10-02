/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ban;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.ban.commands.BanCommand;
import io.github.nucleuspowered.nucleus.modules.ban.commands.CheckBanCommand;
import io.github.nucleuspowered.nucleus.modules.ban.commands.TempBanCommand;
import io.github.nucleuspowered.nucleus.modules.ban.commands.UnbanCommand;
import io.github.nucleuspowered.nucleus.modules.ban.config.BanConfig;
import io.github.nucleuspowered.nucleus.modules.ban.infoprovider.BackInfoProvider;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.playerinformation.NucleusProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class BanModule implements IModule.Configurable<BanConfig> {

    public static final String ID = "ban";


    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

    }

    @Override
    public Optional<NucleusProvider> getInfoProvider() {
        return Optional.of(new BackInfoProvider());
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                BanCommand.class,
                CheckBanCommand.class,
                TempBanCommand.class,
                UnbanCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(BanPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Class<? extends TaskBase>> getTasks() {
        return Collections.emptyList();
    }

    @Override
    public Class<BanConfig> getConfigClass() {
        return BanConfig.class;
    }

}
