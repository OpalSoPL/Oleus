/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ban;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.ban.commands.BanCommand;
import io.github.nucleuspowered.nucleus.modules.ban.commands.CheckBanCommand;
import io.github.nucleuspowered.nucleus.modules.ban.commands.TempBanCommand;
import io.github.nucleuspowered.nucleus.modules.ban.commands.UnbanCommand;
import io.github.nucleuspowered.nucleus.modules.ban.config.BanConfig;
import io.github.nucleuspowered.nucleus.modules.ban.infoprovider.BanInfoProvider;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.playerinformation.NucleusProvider;

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
        return Optional.of(new BanInfoProvider());
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
    public Collection<Class<? extends TaskBase>> getAsyncTasks() {
        return Collections.emptyList();
    }

    @Override
    public Class<BanConfig> getConfigClass() {
        return BanConfig.class;
    }

}
