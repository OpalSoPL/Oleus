/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kick.commands;

import io.github.nucleuspowered.nucleus.modules.kick.KickPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;

import java.util.stream.Collectors;

@EssentialsEquivalent("kickall")
@Command(
        aliases = "kickall",
        basePermission = KickPermissions.BASE_KICKALL,
        commandDescriptionKey = "kickall",
        associatedPermissionLevelKeys = KickPermissions.KICKALL_WHITELIST
)
public class KickAllCommand implements ICommandExecutor {

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.builder().setRequirement(c -> serviceCollection.permissionService().hasPermission(c, KickPermissions.KICKALL_WHITELIST))
                    .alias("f")
                    .alias("w")
                    .build()
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_REASON_COMPONENT
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Component r = context.getOne(NucleusParameters.OPTIONAL_REASON_COMPONENT)
                .orElseGet(() -> context.getMessage("command.kick.defaultreason"));
        final boolean f = context.hasFlag("f");

        if (f) {
            Sponge.getServer().setHasWhitelist(true);
        }

        // Don't kick self
        Sponge.getServer().getOnlinePlayers().stream()
                .filter(context::is)
                .collect(Collectors.toList())
                .forEach(x -> x.kick(r));

        // MessageChannel mc = MessageChannel.fixed(Sponge.getServer().getConsole(), src);
        final SystemSubject console = Sponge.getSystemSubject();
        context.sendMessage("command.kickall.message");
        context.sendMessageTo(console, "command.kickall.message");
        context.sendMessage("command.reason", r);
        context.sendMessageTo(console, "command.reason", r);
        if (f) {
            context.sendMessage("command.kickall.whitelist");
            context.sendMessageTo(console, "command.kickall.whitelist");
        }

        return context.successResult();
    }
}
