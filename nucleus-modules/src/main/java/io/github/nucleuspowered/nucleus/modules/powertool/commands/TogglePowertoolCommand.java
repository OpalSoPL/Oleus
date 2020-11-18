/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.commands;

import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.userprefs.NucleusKeysProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IUserPreferenceService;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
@Command(
        aliases = {"toggle"},
        basePermission = PowertoolPermissions.BASE_POWERTOOL,
        commandDescriptionKey = "powertool.toggle"
)
public class TogglePowertoolCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player src = context.getCommandSourceRoot();
        final IUserPreferenceService ups = context.getServiceCollection().userPreferenceService();
        final boolean keys = ups.get(src.getUniqueId(), NucleusKeysProvider.POWERTOOL_ENABLED).orElse(true);

        // If specified - get the key. Else, the inverse of what we have now.
        final boolean toggle = context.getOne(NucleusParameters.Keys.BOOL, Boolean.class).orElse(!keys);
        ups.set(src.getUniqueId(), NucleusKeysProvider.POWERTOOL_ENABLED, toggle);

        context.sendMessage("command.powertool.toggle", context.getMessage(toggle ? "standard.enabled" : "standard.disabled"));
        return context.successResult();
    }

}
