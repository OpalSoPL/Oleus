/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nameban;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.nameban.commands.NameBanCommand;
import io.github.nucleuspowered.nucleus.modules.nameban.commands.NameUnbanCommand;
import io.github.nucleuspowered.nucleus.modules.nameban.config.NameBanConfig;
import io.github.nucleuspowered.nucleus.modules.nameban.listeners.NameBanListener;
import io.github.nucleuspowered.nucleus.modules.nameban.services.NameBanHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class NameBanModule implements IModule.Configurable<NameBanConfig> {

    public static final String ID = "nameban";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(NameBanHandler.class, new NameBanHandler(serviceCollection), false);
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                NameBanCommand.class,
                NameUnbanCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(NameBanPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(NameBanListener.class);
    }

    @Override public Class<NameBanConfig> getConfigClass() {
        return NameBanConfig.class;
    }
}
