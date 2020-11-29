/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.module.message.target.MessageTarget;
import io.github.nucleuspowered.nucleus.api.module.message.target.UserMessageTarget;
import io.github.nucleuspowered.nucleus.modules.message.MessagePermissions;
import io.github.nucleuspowered.nucleus.modules.message.config.MessageConfig;
import io.github.nucleuspowered.nucleus.modules.message.parameter.CustomTargetParameter;
import io.github.nucleuspowered.nucleus.modules.message.services.MessageHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import io.vavr.control.Either;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@EssentialsEquivalent({"msg", "tell", "m", "t", "whisper"})
@Command(
        aliases = { "message", "m", "msg", "whisper", "w", "t" },
        basePermission = MessagePermissions.BASE_MESSAGE,
        commandDescriptionKey = "message",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = MessagePermissions.EXEMPT_COOLDOWN_MESSAGE),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = MessagePermissions.EXEMPT_WARMUP_MESSAGE),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = MessagePermissions.EXEMPT_COST_MESSAGE)
        },
        associatedPermissions = {
                MessagePermissions.MESSAGE_COLOUR,
                MessagePermissions.MESSAGE_STYLE,
                MessagePermissions.MESSAGE_URLS
        }
)
public class MessageCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final MessageHandler messageHandler;
    boolean canMessageSelf = false;

    private final Parameter.Value<MessageTarget> customTargetParameter;

    @Inject
    public MessageCommand(final INucleusServiceCollection nucleusServiceCollection) {
        this.messageHandler = nucleusServiceCollection.getServiceUnchecked(MessageHandler.class);
        this.customTargetParameter =  Parameter.builder(MessageTarget.class)
                .setKey("custom target")
                .parser(new CustomTargetParameter(this.messageHandler))
                .build();
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                Parameter.firstOf(
                        this.customTargetParameter,
                        NucleusParameters.Composite.PLAYER_OR_CONSOLE
                ),
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final MessageTarget messageTarget;
        if (context.hasAny(this.customTargetParameter)) {
            messageTarget = context.requireOne(this.customTargetParameter);
        } else {
            final Either<SystemSubject, ServerPlayer> target = NucleusParameters.Composite.parsePlayerOrConsole(context);
            if (context.is(target.fold(Function.identity(), Function.identity()))) {
                return context.errorResult("command.message.self");
            }

            messageTarget = target.fold(
                    x -> this.messageHandler.getSystemMessageTarget(),
                    x -> this.messageHandler.getUserMessageTarget(x.getUniqueId()).get());
        }
        final MessageTarget sender;
        final Optional<UUID> uuidOptional = context.getUniqueId();
        if (uuidOptional.isPresent()) {
            sender = this.messageHandler.getUserMessageTarget(uuidOptional.get()).get();
        } else {
            sender = this.messageHandler.getSystemMessageTarget();
        }

        return MessageCommand.executeCommon(this.messageHandler, context, sender, messageTarget);
    }

    static ICommandResult executeCommon(final MessageHandler messageHandler, final ICommandContext context, final MessageTarget sender,
            final MessageTarget receiver) {

        if (receiver instanceof UserMessageTarget) {
            NucleusAPI.getAFKService().ifPresent(x -> x.notifyIsAfk(context.getAudience(), ((UserMessageTarget) receiver).getUserUUID()));
        }

        final boolean b = messageHandler.sendMessage(sender, receiver, context.requireOne(NucleusParameters.MESSAGE));
        return b ? context.successResult() : context.failResult();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.canMessageSelf = serviceCollection.configProvider().getModuleConfig(MessageConfig.class).isCanMessageSelf();
    }

}
