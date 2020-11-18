/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.commands;

import io.github.nucleuspowered.nucleus.modules.jail.JailPermissions;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@EssentialsEquivalent({"setjail", "createjail"})
@Command(
        aliases = { "set", "#setjail", "#createjail" },
        basePermission = JailPermissions.BASE_JAILS_SET,
        commandDescriptionKey = "jails.set",
        parentCommand = JailsCommand.class)
public class SetJailCommand implements ICommandExecutor {

    private final Parameter.Value<String> parameter = Parameter.string().setKey("jail").build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.parameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String name = context.requireOne(this.parameter).toLowerCase();
        final JailService handler = context.getServiceCollection().getServiceUnchecked(JailService.class);
        if (handler.getJail(name).isPresent()) {
            return context.errorResult("command.jails.set.exists", name);
        }

        final ServerPlayer src = context.getIfPlayer();
        if (handler.setJail(name, src.getServerLocation(), src.getRotation()).isPresent()) {
            context.sendMessage("command.jails.set.success", name);
            return context.successResult();
        } else {
            return context.errorResult("command.jails.set.error", name);
        }
    }
}
