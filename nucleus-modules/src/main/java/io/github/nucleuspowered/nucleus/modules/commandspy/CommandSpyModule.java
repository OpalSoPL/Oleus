/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandspy;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.commandspy.config.CommandSpyConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;

import java.util.Collection;
import java.util.Optional;

public final class CommandSpyModule implements IModule.Configurable<CommandSpyConfig> {

    public final static String ID = "command-spy";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {

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

    @Override public Collection<Class<? extends TaskBase>> getTasks() {
        return null;
    }

    @Override public Class<CommandSpyConfig> getConfigClass() {
        return CommandSpyConfig.class;
    }

    @Listener
    public void registerUserPreferenceKey(final RegisterCatalogEvent<NucleusUserPreferenceService.PreferenceKey<?>> event) {
        event.register(new PreferenceKeyImpl.BooleanKey(
                NucleusKeysProvider.COMMAND_SPY_KEY,
                true,
                CommandSpyPermissions.BASE_COMMANDSPY,
                "userpref.commandspy"
        ));
    }
}
