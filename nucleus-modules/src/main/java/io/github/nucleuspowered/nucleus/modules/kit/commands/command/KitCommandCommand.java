/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands.command;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.commands.KitCommand;
import io.github.nucleuspowered.nucleus.modules.kit.parameters.KitParameter;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import java.util.List;

@Command(
        aliases = { "command", "commands" },
        basePermission = KitPermissions.BASE_KIT_COMMAND,
        commandDescriptionKey = "kit.command",
        async = true,
        parentCommand = KitCommand.class
)
public class KitCommandCommand implements ICommandExecutor {

    /*private final String removePermission = Nucleus.getNucleus().getPermissionRegistry()
            .getPermissionsForNucleusCommand(KitRemoveCommandCommand.class).getBase(); */
    private final TextComponent removeIcon = Text.of(TextColors.WHITE, "[", TextColors.DARK_RED, "X", TextColors.WHITE, "]");

    @Override public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.getServiceUnchecked(KitService.class).createKitElement(true)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        // List all commands on a kit.
        final Kit kit = context.requireOne(KitParameter.KIT_PARAMETER_KEY, Kit.class);
        final List<String> commands = kit.getCommands();

        if (commands.isEmpty()) {
            context.sendMessage("command.kit.command.nocommands", kit.getName());
        } else {
            final List<Text> cc = Lists.newArrayList();
            for (int i = 0; i < commands.size(); i++) {
                TextComponent t = context.getMessage("command.kit.command.commands.entry", i + 1, commands.get(i));
                if (context.testPermission(KitPermissions.BASE_KIT_COMMAND_REMOVE)) {
                    t = Text.of(
                            Text.builder().append(this.removeIcon)
                                .onClick(TextActions.runCommand("/nucleus:kit command remove " + kit.getName() + " " + commands.get(i)))
                                .onHover(TextActions.showText(context.getMessage("command.kit.command.removehover"))).build(), " ", t);
                }

                cc.add(t);
            }

            Util.getPaginationBuilder(context.getCommandSourceRoot())
                .title(context.getMessage("command.kit.command.commands.title", kit.getName()))
                .contents(cc)
                .sendTo(context.getCommandSourceRoot());
        }

        return context.successResult();
    }
}
