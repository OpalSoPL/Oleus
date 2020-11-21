/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.module.IModule;
import io.github.nucleuspowered.nucleus.modules.powertool.commands.DeletePowertoolCommand;
import io.github.nucleuspowered.nucleus.modules.powertool.commands.ListPowertoolCommand;
import io.github.nucleuspowered.nucleus.modules.powertool.commands.PowertoolCommand;
import io.github.nucleuspowered.nucleus.modules.powertool.commands.TogglePowertoolCommand;
import io.github.nucleuspowered.nucleus.modules.powertool.listeners.PowertoolListener;
import io.github.nucleuspowered.nucleus.modules.powertool.services.PowertoolService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.PreferenceKeyImpl;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCatalogEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class PowertoolModule implements IModule {

    public static final String ID = "powertool";

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        serviceCollection.registerService(PowertoolService.class, new PowertoolService(serviceCollection), false);
    }

    @Override public Collection<Class<? extends ICommandExecutor>> getCommands() {
        return Arrays.asList(
                DeletePowertoolCommand.class,
                ListPowertoolCommand.class,
                PowertoolCommand.class,
                TogglePowertoolCommand.class
        );
    }

    @Override public Optional<Class<?>> getPermissions() {
        return Optional.of(PowertoolPermissions.class);
    }

    @Override public Collection<Class<? extends ListenerBase>> getListeners() {
        return Collections.singleton(PowertoolListener.class);
    }

    @Listener
    public void onPreferenceKeyRegistration(final RegisterCatalogEvent<NucleusUserPreferenceService.PreferenceKey<?>> event) {
        event.register(PowertoolKeys.POWERTOOL_ENABLED);
    }
}
