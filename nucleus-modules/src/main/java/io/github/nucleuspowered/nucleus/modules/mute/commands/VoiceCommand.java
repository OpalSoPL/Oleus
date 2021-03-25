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
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.UUID;

@Command(aliases = { "voice" }, basePermission = MutePermissions.BASE_VOICE, commandDescriptionKey = "voice")
public class VoiceCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONE_PLAYER,
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final MuteService muteHandler = context.getServiceCollection().getServiceUnchecked(MuteService.class);
        if (!muteHandler.isGlobalMuteEnabled()) {
            return context.errorResult("command.voice.globaloff");
        }

        final ServerPlayer pl = context.requireOne(NucleusParameters.ONE_PLAYER);
        if (context.testPermissionFor(pl, MutePermissions.VOICE_AUTO)) {
            return context.errorResult("command.voice.autovoice", pl.name());
        }

        final boolean turnOn = context.getOne(NucleusParameters.OPTIONAL_ONE_TRUE_FALSE).orElseGet(() -> !muteHandler.isVoiced(pl.uniqueId()));

        final UUID voice = pl.uniqueId();
        if (turnOn == muteHandler.isVoiced(voice)) {
            if (turnOn) {
                context.sendMessage("command.voice.alreadyvoiced", pl.name());
            } else {
                context.sendMessage("command.voice.alreadynotvoiced", pl.name());
            }

            return context.failResult();
        }

        final Audience mmc =
                Audience.audience(
                    context.getServiceCollection().permissionService().permissionMessageChannel(MutePermissions.VOICE_NOTIFY),
                    context.audience());

        if (turnOn) {
            muteHandler.addVoice(pl.uniqueId());
            mmc.sendMessage(context.getMessage("command.voice.voiced.source", pl.name()));
            context.sendMessageTo(pl, "command.voice.voiced.target");
        } else {
            muteHandler.removeVoice(pl.uniqueId());
            mmc.sendMessage(context.getMessage("command.voice.voiced.source", pl.name()));
            context.sendMessageTo(pl, "command.voice.voiced.target");
        }

        return context.successResult();
    }
}
