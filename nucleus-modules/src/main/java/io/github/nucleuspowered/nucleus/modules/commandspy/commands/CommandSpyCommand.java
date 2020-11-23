/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.commandspy.commands;

import io.github.nucleuspowered.nucleus.modules.commandspy.CommandSpyKeys;
import io.github.nucleuspowered.nucleus.modules.commandspy.CommandSpyPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IUserPreferenceService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import java.util.UUID;

@Command(
        aliases = "commandspy",
        basePermission = CommandSpyPermissions.BASE_COMMANDSPY,
        commandDescriptionKey = "commandspy"
)
public class CommandSpyCommand implements ICommandExecutor {

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final IUserPreferenceService userPreferenceService = context.getServiceCollection().userPreferenceService();
        final UUID uuid = context.getUniqueId().orElseThrow(() -> new CommandException(Component.text("No UUID was found")));
        final boolean to =
                context.getOne(NucleusParameters.ONE_TRUE_FALSE)
                    .orElseGet(() -> !userPreferenceService.getUnwrapped(
                            uuid,
                            CommandSpyKeys.COMMAND_SPY));
        userPreferenceService.set(uuid, CommandSpyKeys.COMMAND_SPY, to);
        // "loc:" indicates to the engine that the text in the key is localisable
        context.sendMessage("command.commandspy.success", to ? "loc:standard.enabled" : "loc:standard.disabled");

        return context.successResult();
    }
}
