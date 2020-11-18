/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ignore.commands;

import io.github.nucleuspowered.nucleus.modules.ignore.IgnorePermissions;
import io.github.nucleuspowered.nucleus.modules.ignore.services.IgnoreService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

@EssentialsEquivalent("ignore")
@Command(
        aliases = { "ignore" },
        basePermission = IgnorePermissions.BASE_IGNORE,
        commandDescriptionKey = "ignore",
        associatedPermissions = IgnorePermissions.IGNORE_CHAT
)
public class IgnoreCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONE_USER,
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Get the target
        final User target = context.requireOne(NucleusParameters.ONE_USER);
        final Player player = context.getIfPlayer();

        if (context.is(target)) {
            return context.errorResult("command.ignore.self");
        }

        final IgnoreService ignoreService = context.getServiceCollection().getServiceUnchecked(IgnoreService.class);
        if (context.testPermissionFor(target, "exempt.chat")) {
            // Make sure they are removed.
            ignoreService.unignore(player.getUniqueId(), target.getUniqueId());
            return context.errorResult("command.ignore.exempt", target.getName());
        }

        // Ok, we can ignore or unignore them.
        final boolean ignore = context.getOne(NucleusParameters.OPTIONAL_ONE_TRUE_FALSE)
                .orElseGet(() -> !ignoreService.isIgnored(player.getUniqueId(), target.getUniqueId()));

        if (ignore) {
            ignoreService.ignore(player.getUniqueId(), target.getUniqueId());
            context.sendMessage("command.ignore.added", target.getName());
        } else {
            ignoreService.unignore(player.getUniqueId(), target.getUniqueId());
            context.sendMessage("command.ignore.remove", target.getName());
        }

        return context.successResult();
    }
}
