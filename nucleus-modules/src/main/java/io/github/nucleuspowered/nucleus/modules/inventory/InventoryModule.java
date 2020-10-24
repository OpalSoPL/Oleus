/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.inventory;

import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.inventory.commands.ClearInventoryCommand;
import io.github.nucleuspowered.nucleus.modules.inventory.commands.EnderChestCommand;
import io.github.nucleuspowered.nucleus.modules.inventory.commands.InvSeeCommand;
import io.github.nucleuspowered.nucleus.modules.inventory.listeners.KeepInventoryListener;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class InventoryModule implements IModule {

    public static final String ID = "inventory";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
    }

    @Override
    public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                ClearInventoryCommand.class,
                EnderChestCommand.class,
                InvSeeCommand.class
        );
    }

    @Override
    public Optional<Class<?>> getPermissions() {
        return Optional.of(InventoryPermissions.class);
    }

    @Override
    public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(KeepInventoryListener.class);
    }

}
