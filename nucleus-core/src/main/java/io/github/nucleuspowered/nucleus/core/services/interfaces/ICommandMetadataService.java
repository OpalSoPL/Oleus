/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.commandmetadata.CommandMetadataService;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;

import java.util.Collection;
import java.util.Optional;

@ImplementedBy(CommandMetadataService.class)
public interface ICommandMetadataService {


    void registerCommands(
            String id,
            String name,
            Collection<? extends Class<? extends ICommandExecutor>> associatedContext);

    void registerCommand(
            String id,
            String name,
            Class<? extends ICommandExecutor> associatedContext
    );

    void completeRegistrationPhase(INucleusServiceCollection serviceCollection,
            RegisterCommandEvent<org.spongepowered.api.command.Command.Parameterized> event);

    void reset();

    Optional<CommandControl> getControl(Class<? extends ICommandExecutor> executorClass);

    Collection<CommandControl> getCommands();

    Collection<CommandControl> getCommandsAndSubcommands();

    CommandControl getControl(String primaryAlias);
}
