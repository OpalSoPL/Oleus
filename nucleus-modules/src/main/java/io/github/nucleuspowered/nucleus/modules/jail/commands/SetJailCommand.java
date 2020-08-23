/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.commands;

import io.github.nucleuspowered.nucleus.modules.jail.JailPermissions;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
@EssentialsEquivalent({"setjail", "createjail"})
@Command(
        aliases = { "set", "#setjail", "#createjail" },
        basePermission = JailPermissions.BASE_JAILS_SET,
        commandDescriptionKey = "jails.set",
        parentCommand = JailsCommand.class,
        async = true
)
public class SetJailCommand implements ICommandExecutor {

    private final String jailName = "jail";

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                GenericArguments.onlyOne(GenericArguments.string(Text.of(this.jailName)))
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String name = context.requireOne(this.jailName, String.class).toLowerCase();
        final JailHandler handler = context.getServiceCollection().getServiceUnchecked(JailHandler.class);
        if (handler.getJail(name).isPresent()) {
            return context.errorResult("command.jails.set.exists", name);
        }

        final Player src = context.getIfPlayer();
        if (handler.setJail(name, src.getLocation(), src.getRotation())) {
            context.sendMessage("command.jails.set.success", name);
            return context.successResult();
        } else {
            return context.errorResult("command.jails.set.error", name);
        }
    }
}
