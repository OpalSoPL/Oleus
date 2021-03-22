/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.commands;

import io.github.nucleuspowered.nucleus.modules.mute.MutePermissions;
import io.github.nucleuspowered.nucleus.modules.mute.services.MuteService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@Command(aliases = "globalmute", basePermission = MutePermissions.BASE_GLOBALMUTE, commandDescriptionKey = "globalmute")
public class GlobalMuteCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final MuteService muteHandler = context.getServiceCollection().getServiceUnchecked(MuteService.class);
        final boolean turnOn = context.getOne(NucleusParameters.OPTIONAL_ONE_TRUE_FALSE).orElse(!muteHandler.isGlobalMuteEnabled());

        muteHandler.setGlobalMuteEnabled(turnOn);
        final String onOff = context.getMessageString(turnOn ? "standard.enabled" : "standard.disabled");
        context.sendMessage("command.globalmute.status", onOff);
        final String key = "command.globalmute.broadcast." + (turnOn ? "enabled" : "disabled");
        for (final ServerPlayer player : Sponge.server().onlinePlayers()) {
            context.sendMessageTo(player, key);
        }
        context.sendMessageTo(Sponge.systemSubject(), key);
        return context.successResult();
    }
}
