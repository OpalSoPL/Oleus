/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo;

import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.modules.playerinfo.commands.GetFromIpCommand;
import io.github.nucleuspowered.nucleus.modules.playerinfo.commands.GetPosCommand;
import io.github.nucleuspowered.nucleus.modules.playerinfo.commands.ListPlayerCommand;
import io.github.nucleuspowered.nucleus.modules.playerinfo.commands.NearCommand;
import io.github.nucleuspowered.nucleus.modules.playerinfo.commands.SeenCommand;
import io.github.nucleuspowered.nucleus.modules.playerinfo.config.PlayerInfoConfig;
import io.github.nucleuspowered.nucleus.modules.playerinfo.listeners.CommandListener;
import io.github.nucleuspowered.nucleus.modules.playerinfo.services.SeenHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class PlayerInfoModule implements IModule.Configurable<PlayerInfoConfig> {

    public static final String ID = "playerinfo";

    @Override public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(SeenHandler.class, new SeenHandler(serviceCollection), false);
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                GetFromIpCommand.class,
                GetPosCommand.class,
                ListPlayerCommand.class,
                NearCommand.class,
                SeenCommand.class
        );
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(PlayerInfoPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(CommandListener.class);
    }

    @Override public Class<PlayerInfoConfig> getConfigClass() {
        return PlayerInfoConfig.class;
    }
}
