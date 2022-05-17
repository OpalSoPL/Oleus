/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands.command;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.commands.KitCommand;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.ArrayList;
import java.util.List;

@Command(
        aliases = { "command", "commands" },
        basePermission = KitPermissions.BASE_KIT_COMMAND,
        commandDescriptionKey = "kit.command",
        parentCommand = KitCommand.class
)
public class KitCommandCommand extends KitCommandCommandBase {

    private final Component removeIcon = LinearComponents.linear(
            Component.text("[", NamedTextColor.WHITE),
            Component.text("X", NamedTextColor.DARK_RED),
            Component.text("]", NamedTextColor.WHITE)
    );

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(KitService.class).kitParameterWithPermission()
        };
    }

    @Override
    protected ICommandResult execute0(final ICommandContext context) throws CommandException {
        // List all commands on a kit.
        final Kit kit = context.requireOne(KitService.KIT_KEY);
        final List<String> commands = kit.getCommands();

        if (commands.isEmpty()) {
            context.sendMessage("command.kit.command.nocommands", kit.getName());
        } else {
            final List<Component> cc = new ArrayList<>();
            for (int i = 0; i < commands.size(); i++) {
                Component t = context.getMessage("command.kit.command.commands.entry", i + 1, commands.get(i));
                if (context.testPermission(KitPermissions.BASE_KIT_COMMAND_REMOVE)) {
                    t = LinearComponents.linear(
                            this.removeIcon
                                    .clickEvent(ClickEvent.runCommand("/nucleus:kit command remove " + kit.getName() + " " + commands.get(i)))
                                    .hoverEvent(HoverEvent.showText(context.getMessage("command.kit.command.removehover"))),
                            Component.space(),
                            t);
                }

                cc.add(t);
            }

            Util.getPaginationBuilder(context.audience())
                .title(context.getMessage("command.kit.command.commands.title", kit.getName()))
                .contents(cc)
                .sendTo(context.audience());
        }

        return context.successResult();
    }
}
