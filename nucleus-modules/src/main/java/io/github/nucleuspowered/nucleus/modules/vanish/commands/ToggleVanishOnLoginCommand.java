/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.vanish.commands;

import io.github.nucleuspowered.nucleus.modules.vanish.VanishPermissions;
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

import java.util.UUID;

@Command(
        aliases = {"vanishonlogin", "vonlogin"},
        basePermission = VanishPermissions.VANISH_ONLOGIN,
        commandDescriptionKey = "vanishonlogin")
public class ToggleVanishOnLoginCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final IUserPreferenceService ups = context.getServiceCollection().userPreferenceService();
        final UUID uuid = context.getIfPlayer().getUniqueId();
        final boolean keys = ups.get(uuid, NucleusKeysProvider.VANISH_ON_LOGIN).orElse(true);

        // If specified - get the key. Else, the inverse of what we have now.
        final boolean toggle = context.getOne(NucleusParameters.Keys.BOOL, Boolean.class).orElse(!keys);
        ups.set(uuid, NucleusKeysProvider.VANISH_ON_LOGIN, toggle);

        context.sendMessage("command.vanishonlogin.toggle", toggle ? "loc:standard.enabled" : "loc:standard.disabled");
        return context.successResult();
    }

}
