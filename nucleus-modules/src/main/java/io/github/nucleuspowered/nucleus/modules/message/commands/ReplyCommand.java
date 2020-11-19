/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.modules.message.MessagePermissions;
import io.github.nucleuspowered.nucleus.modules.message.services.MessageHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.NotifyIfAFK;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.parameter.Parameter;
/**
 * Replies to the last player who sent a message.
 */
@EssentialsEquivalent({"r", "reply"})
@NotifyIfAFK(NucleusParameters.Keys.PLAYER) // TODO: Better way to do this
@Command(
        aliases = {"reply", "r"},
        basePermission = MessagePermissions.BASE_MESSAGE,
        commandDescriptionKey = "reply",
        modifierOverride = "message",
        hasHelpCommand = false,
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = MessagePermissions.EXEMPT_COOLDOWN_MESSAGE),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = MessagePermissions.EXEMPT_WARMUP_MESSAGE),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = MessagePermissions.EXEMPT_COST_MESSAGE)
        }
)
public class ReplyCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final MessageHandler messageHandler = context.getServiceCollection().getServiceUnchecked(MessageHandler.class);
        final boolean b = messageHandler.replyMessage(context.getUniqueId().orElse(Util.CONSOLE_FAKE_UUID), context.requireOne(NucleusParameters.MESSAGE));
        if (b) {
            NucleusAPI.getAFKService().map(x -> x.isAFK())
            // For Notify on AFK - TODO: Better way to do this
            /* UUID uuid = context.getUniqueId().orElse(Util.CONSOLE_FAKE_UUID);
            this.handler.getLastMessageFrom(uuid).ifPresent(x -> args.putArg(NucleusParameters.Keys.PLAYER, x)); */
            return context.successResult();
        }

        return context.failResult();
    }
}
