/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.commands;

import io.github.nucleuspowered.nucleus.api.EventContexts;
import io.github.nucleuspowered.nucleus.api.util.NoExceptionAutoClosable;
import io.github.nucleuspowered.nucleus.modules.message.HelpOpMessageChannel;
import io.github.nucleuspowered.nucleus.modules.message.MessagePermissions;
import io.github.nucleuspowered.nucleus.modules.message.config.MessageConfig;
import io.github.nucleuspowered.nucleus.modules.message.events.InternalNucleusHelpOpEvent;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IChatMessageFormatterService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import com.google.inject.Inject;

@EssentialsEquivalent({"helpop", "amsg", "ac"})
@Command(
        aliases = { "helpop" },
        basePermission = MessagePermissions.BASE_HELPOP,
        commandDescriptionKey = "helpop",
        async = true,
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = MessagePermissions.EXEMPT_COOLDOWN_HELPOP),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = MessagePermissions.EXEMPT_WARMUP_HELPOP),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = MessagePermissions.EXEMPT_COST_HELPOP)
        },
        associatedPermissions = MessagePermissions.HELPOP_RECEIVE
)
public class HelpOpCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final IChatMessageFormatterService chatMessageFormatterService;
    @Nullable private HelpOpMessageChannel channel;

    @Inject
    public HelpOpCommand(final INucleusServiceCollection serviceCollection) {
        this.chatMessageFormatterService = serviceCollection.chatMessageFormatter();
    }

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.MESSAGE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String message = context.requireOne(NucleusParameters.Keys.MESSAGE, String.class);

        // Message is about to be sent. Send the event out. If canceled, then
        // that's that.
        if (Sponge.getEventManager().post(new InternalNucleusHelpOpEvent(context.getCommandSourceRoot(), message))) {
            return context.errorResult("message.cancel");
        }

        final Player player = context.getIfPlayer();

        final MessageChannelEvent.Chat chat;
        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame();
                final NoExceptionAutoClosable c = this.chatMessageFormatterService.setPlayerNucleusChannelTemporarily(player.getUniqueId(), this.channel)) {
            frame.addContext(EventContexts.SHOULD_FORMAT_CHANNEL, false);
            frame.pushCause(player);
            chat = player.simulateChat(Text.of(message), Sponge.getCauseStackManager().getCurrentCause());
        }

        if (chat.isCancelled()) {
            context.sendMessage("command.helpop.fail");
        } else {
            context.sendMessage("command.helpop.success");
        }

        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.channel = new HelpOpMessageChannel(
                serviceCollection.configProvider().getModuleConfig(MessageConfig.class).getHelpOpPrefix(serviceCollection.textTemplateFactory()),
                serviceCollection.permissionService(),
                serviceCollection.textStyleService()
        );
    }
}
