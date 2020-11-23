/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.message.commands;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.modules.message.MessagePermissions;
import io.github.nucleuspowered.nucleus.modules.message.config.MessageConfig;
import io.github.nucleuspowered.nucleus.modules.message.events.InternalNucleusHelpOpEvent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.ITextStyleService;
import io.github.nucleuspowered.nucleus.core.util.PermissionMessageChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.CauseStackManager;

@EssentialsEquivalent({"helpop", "amsg", "ac"})
@Command(
        aliases = { "helpop" },
        basePermission = MessagePermissions.BASE_HELPOP,
        commandDescriptionKey = "helpop",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = MessagePermissions.EXEMPT_COOLDOWN_HELPOP),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = MessagePermissions.EXEMPT_WARMUP_HELPOP),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = MessagePermissions.EXEMPT_COST_HELPOP)
        },
        associatedPermissions = MessagePermissions.HELPOP_RECEIVE
)
public class HelpOpCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private NucleusTextTemplate prefix = NucleusTextTemplateImpl.empty();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String message = context.requireOne(NucleusParameters.MESSAGE);
        final ServerPlayer player = context.requirePlayer();

        try (final CauseStackManager.StackFrame frame = Sponge.getServer().getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(player);
            // Message is about to be sent. Send the event out. If canceled, then
            // that's that.
            if (Sponge.getEventManager().post(new InternalNucleusHelpOpEvent(message))) {
                return context.errorResult("message.cancel");
            }
        }

        new PermissionMessageChannel(context.getServiceCollection().permissionService(), MessagePermissions.HELPOP_RECEIVE)
                .sendMessage(player, this.formatMessage(context, Component.text(message)));

        context.sendMessage("command.helpop.success");
        return context.successResult();
    }

    private Component formatMessage(final ICommandContext source, final Component body) {
        final Component prefixComponent;
        if (this.prefix != null) {
            prefixComponent = this.prefix.getForObject(source);
        } else {
            prefixComponent = Component.empty();
        }

        final ITextStyleService.TextFormat format = source.getServiceCollection().textStyleService().getLastColourAndStyle(prefixComponent, null);
        return LinearComponents.linear(
                prefixComponent,
                Component.text().color(format.colour().orElse(null)).style(format.style()).append(body).build()
        );
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.prefix = serviceCollection.configProvider().getModuleConfig(MessageConfig.class).getHelpOpPrefix(serviceCollection.textTemplateFactory());
    }
}
