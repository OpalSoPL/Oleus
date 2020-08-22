/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.control;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class UsageCommand {

    private final CommandControl attachedCommandControl;

    UsageCommand(final CommandControl attachedCommandControl) {
        this.attachedCommandControl = attachedCommandControl;
    }

    void process(final ICommandContext<? extends CommandSource> commandContext, final String command, @Nullable final String previous) throws CommandException {
        if (!this.attachedCommandControl.testPermission(commandContext.getCommandSourceUnchecked())) {
            commandContext.sendMessage("command.usage.nopermission");
            return;
        }

        try {
            final List<Text> textMessages = this.usage(commandContext, command, previous);

            // Header
            // String command = this.attachedCommandControl.getCommand();
            final Text header = commandContext.getMessage( "command.usage.header", command);

            Util.getPaginationBuilder(commandContext.getCommandSource()).title(header).contents(textMessages).sendTo(commandContext.getCommandSource());
        } catch (final CommandPermissionException e) {
            commandContext.sendMessage("command.usage.nopermission");
        }
    }

    public List<Text> usage(final ICommandContext<? extends CommandSource> context, final String command, @Nullable final String previous) throws CommandPermissionException {
        if (!this.attachedCommandControl.testPermission(context.getCommandSourceUnchecked())) {
            throw new CommandPermissionException();
        }

        final List<Text> textMessages = Lists.newArrayList();

        if (previous != null) {
            textMessages.add(context.getMessage("command.usage.noexist", previous));
            textMessages.add(Util.SPACE);
        }

        if (this.attachedCommandControl.getSourceType() == Player.class) {
            textMessages.add(context.getMessage("command.usage.playeronly"));
        }

        textMessages.add(context.getMessage("command.usage.module",
                this.attachedCommandControl.getMetadata().getModulename(),
                this.attachedCommandControl.getMetadata().getModuleid()));

        textMessages.add(context.getMessage("command.usage.context",
                this.attachedCommandControl.getContext().getKey(),
                this.attachedCommandControl.getContext().getValue()));

        // Aliases
        final String prefix = command.replaceAll("[\\S]+$","");
        final String result = "/" + this.attachedCommandControl.getAliases()
                .stream()
                .map(x -> {
                    if (x.startsWith("#")) {
                        return x.substring(1);
                    } else {
                        return prefix + x;
                    }
                }).collect(Collectors.joining(", /"));
        textMessages.add(context.getMessage("command.usage.aliases", result));

        final Optional<Text> desc = this.attachedCommandControl.getShortDescription(context.getCommandSourceUnchecked());
        if (desc.isPresent()) {
            textMessages.add(Util.SPACE);
            textMessages.add(context.getMessage("command.usage.summary"));
            textMessages.add(desc.get());
        }

        final Optional<Text> ext = this.attachedCommandControl.getExtendedDescription(context.getCommandSourceUnchecked());
        if (ext.isPresent()) {
            textMessages.add(Util.SPACE);
            textMessages.add(context.getMessage("command.usage.description"));
            textMessages.add(ext.get());
        }

        if (this.attachedCommandControl.hasExecutor()) {
            textMessages.add(Util.SPACE);
            textMessages.add(context.getMessage("command.usage.usage"));
            textMessages.add(Text.of(TextColors.WHITE, this.attachedCommandControl.getUsageText(context.getCommandSourceUnchecked())));
        }

        final Text subcommands = this.attachedCommandControl.getSubcommandTexts(context.getCommandSourceUnchecked());
        if (!subcommands.isEmpty()) {
            textMessages.add(Util.SPACE);
            textMessages.add(Text.joinWith(Text.of(" "), context.getMessage("command.usage.subcommand"), subcommands));
        }

        return textMessages;
    }

}
