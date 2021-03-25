/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

@Command(
        aliases = { "info" },
        basePermission = KitPermissions.BASE_KIT_INFO,
        commandDescriptionKey = "kit.info",
        parentCommand = KitCommand.class
)
public class KitInfoCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(KitService.class).kitParameterWithoutPermission()
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Kit kit = context.requireOne(KitService.KIT_KEY);

        Util.getPaginationBuilder(context.audience())
                .title(context.getMessage("command.kit.info.title", kit.getName()))
                .contents(
                        this.addViewHover(context, kit),
                        this.addCommandHover(context, kit),
                        context.getMessage("command.kit.info.sep"),
                        context.getMessage("command.kit.info.firstjoin", this.yesno(context, kit.isFirstJoinKit())),
                        context.getMessage("command.kit.info.cost", String.valueOf(kit.getCost())),
                        context.getMessage("command.kit.info.cooldown", kit.getCooldown()
                                .<Component>map(x -> Component.text(context.getTimeString(x)))
                                .orElseGet(() -> context.getMessage("standard.nocooldown"))),
                        context.getMessage("command.kit.info.onetime", this.yesno(context, kit.isOneTime())),
                        context.getMessage("command.kit.info.autoredeem", this.yesno(context, kit.isAutoRedeem())),
                        context.getMessage("command.kit.info.hidden", this.yesno(context, kit.isHiddenFromList())),
                        context.getMessage("command.kit.info.displayredeem", this.yesno(context, kit.isDisplayMessageOnRedeem())),
                        context.getMessage("command.kit.info.ignoresperm", this.yesno(context, kit.ignoresPermission()))
                ).sendTo(context.audience());
        return context.successResult();
    }

    private Component addViewHover(final ICommandContext context, final Kit kit) {
        return context.getMessage("command.kit.info.itemcount", String.valueOf(kit.getStacks().size()))
                .hoverEvent(HoverEvent.showText(context.getMessage("command.kit.info.hover.itemcount", kit.getName())))
                .clickEvent(ClickEvent.runCommand("/nucleus:kit view " + kit.getName()));
    }

    private Component addCommandHover(final ICommandContext context, final Kit kit) {
        return context.getMessage("command.kit.info.commandcount", String.valueOf(kit.getCommands().size()))
                .hoverEvent(HoverEvent.showText(context.getMessage("command.kit.info.hover.commandcount", kit.getName())))
                .clickEvent(ClickEvent.runCommand("/nucleus:kit command " + kit.getName()));
    }

    private String yesno(final ICommandContext context, final boolean yesno) {
        if (yesno) {
            return context.getMessageString("standard.yesno.true");
        }
        return context.getMessageString("standard.yesno.false");
    }

}
