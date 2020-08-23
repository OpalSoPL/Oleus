/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
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
@Command(
        aliases = { "info" },
        async = true,
        basePermission = KitPermissions.BASE_KIT_INFO,
        commandDescriptionKey = "kit.info",
        parentCommand = KitCommand.class
)
public class KitInfoCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.getServiceUnchecked(KitService.class).createKitElement(false)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Kit kit = context.requireOne(KitParameter.KIT_PARAMETER_KEY, Kit.class);
        

        Util.getPaginationBuilder(context.getCommandSourceRoot())
                .title(context.getMessage("command.kit.info.title", kit.getName()))
                .contents(
                        addViewHover(context, kit),
                        addCommandHover(context, kit),
                        context.getMessage("command.kit.info.sep"),
                        context.getMessage("command.kit.info.firstjoin", yesno(context, kit.isFirstJoinKit())),
                        context.getMessage("command.kit.info.cost", String.valueOf(kit.getCost())),
                        context.getMessage("command.kit.info.cooldown", kit.getCooldown()
                                .<Text>map(x -> Text.of(context.getTimeString(x)))
                                .orElseGet(() -> context.getMessage("standard.nocooldown"))),
                        context.getMessage("command.kit.info.onetime", yesno(context, kit.isOneTime())),
                        context.getMessage("command.kit.info.autoredeem", yesno(context, kit.isAutoRedeem())),
                        context.getMessage("command.kit.info.hidden", yesno(context, kit.isHiddenFromList())),
                        context.getMessage("command.kit.info.displayredeem", yesno(context, kit.isDisplayMessageOnRedeem())),
                        context.getMessage("command.kit.info.ignoresperm", yesno(context, kit.ignoresPermission()))
                ).sendTo(context.getCommandSourceRoot());
        return context.successResult();
    }

    private TextComponent addViewHover(final ICommandContext context, final Kit kit) {
        return context.getMessage("command.kit.info.itemcount", String.valueOf(kit.getStacks().size())).toBuilder()
                .onHover(TextActions.showText(context.getMessage("command.kit.info.hover.itemcount", kit.getName())))
                .onClick(TextActions.runCommand("/nucleus:kit view " + kit.getName())).build();
    }

    private TextComponent addCommandHover(final ICommandContext context, final Kit kit) {
        return context.getMessage("command.kit.info.commandcount", String.valueOf(kit.getCommands().size())).toBuilder()
                .onHover(TextActions.showText(context.getMessage("command.kit.info.hover.commandcount", kit.getName())))
                .onClick(TextActions.runCommand("/nucleus:kit command " + kit.getName())).build();
    }

    private String yesno(final ICommandContext context, final boolean yesno) {
        return context.getMessageString("standard.yesno." + yesno);
    }

}
