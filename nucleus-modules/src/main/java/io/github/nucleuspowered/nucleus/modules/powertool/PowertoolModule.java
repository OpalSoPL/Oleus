/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool;

import io.github.nucleuspowered.nucleus.api.core.event.NucleusRegisterPreferenceKeyEvent;
import io.github.nucleuspowered.nucleus.core.module.IModule;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.listener.ListenerBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.modules.powertool.commands.DeletePowertoolCommand;
import io.github.nucleuspowered.nucleus.modules.powertool.commands.ListPowertoolCommand;
import io.github.nucleuspowered.nucleus.modules.powertool.commands.PowertoolCommand;
import io.github.nucleuspowered.nucleus.modules.powertool.commands.TogglePowertoolCommand;
import io.github.nucleuspowered.nucleus.modules.powertool.listeners.PowertoolListener;
import io.github.nucleuspowered.nucleus.modules.powertool.services.PowertoolService;
import org.spongepowered.api.event.Listener;

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
    public void onPreferenceKeyRegistration(final NucleusRegisterPreferenceKeyEvent event) {
        event.register(PowertoolKeys.POWERTOOL_ENABLED);
    }
}
