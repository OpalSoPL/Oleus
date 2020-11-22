/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.serverlist;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.serverlist.commands.ServerListCommand;
import io.github.nucleuspowered.nucleus.modules.serverlist.commands.TemporaryMessageCommand;
import io.github.nucleuspowered.nucleus.modules.serverlist.config.ServerListConfig;
import io.github.nucleuspowered.nucleus.modules.serverlist.listener.ServerListListener;
import io.github.nucleuspowered.nucleus.modules.serverlist.listener.WhitelistServerListListener;
import io.github.nucleuspowered.nucleus.modules.serverlist.services.ServerListService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class ServerListModule implements IModule.Configurable<ServerListConfig> {

    public static final String ID = "server-list";

    @Override 
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(ServerListService.class, new ServerListService(serviceCollection), false);
    }

    @Override 
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                ServerListCommand.class,
                TemporaryMessageCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(ServerListPermissions.class);
    }

    @Override 
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Arrays.asList(
                ServerListListener.class,
                WhitelistServerListListener.class
        );
    }

    @Override 
    public Class<ServerListConfig> getConfigClass() {
        return ServerListConfig.class;
    }
}
