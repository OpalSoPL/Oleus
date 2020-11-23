/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolPermissions;
import io.github.nucleuspowered.nucleus.modules.powertool.services.PowertoolService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EssentialsEquivalent({"powertool", "pt"})
@Command(
        aliases = {"powertool", "pt"},
        basePermission = PowertoolPermissions.BASE_POWERTOOL,
        commandDescriptionKey = "powertool"
)
public class PowertoolCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_COMMAND
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer src = context.requirePlayer();
        final ItemStack itemStack = src.getItemInHand(HandTypes.MAIN_HAND);
        if (itemStack.isEmpty()) {
            return context.errorResult("command.powertool.noitem");
        }

        final Optional<String> command = context.getOne(NucleusParameters.COMMAND);
        return command
                .map(s -> this.setPowertool(context, src, itemStack.getType(), s))
                .orElseGet(() -> this.viewPowertool(context, src, itemStack));
    }

    private ICommandResult viewPowertool(final ICommandContext context, final ServerPlayer src, final ItemStack item) {
        final Optional<List<String>> cmds = context.getServiceCollection().getServiceUnchecked(PowertoolService.class)
                .getPowertoolForItem(src.getUniqueId(), item.getType());
        if (cmds.isPresent() && !cmds.get().isEmpty()) {
            Util.getPaginationBuilder(context.getAudience())
                    .contents(cmds.get().stream().map(f -> Component.text(f, NamedTextColor.YELLOW)).collect(Collectors.toList()))
                    .title(context.getMessage("command.powertool.viewcmdstitle", item.getType().asComponent(),
                            Component.text(item.getType().getKey().asString())))
                    .sendTo(context.getAudience());
        } else {
            src.sendMessage(context.getMessage("command.powertool.nocmds", item.getType().asComponent()));
        }

        return context.successResult();
    }

    private ICommandResult setPowertool(final ICommandContext context, final Player src, final ItemType item, String command) {
        // For consistency, if a command starts with "/", remove it, but just
        // once. WorldEdit commands can be input using "//"
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        context.getServiceCollection().getServiceUnchecked(PowertoolService.class).setPowertool(src.getUniqueId(), item, Collections.singletonList(command));
        context.sendMessage("command.powertool.set", item.asComponent(), command);
        return context.successResult();
    }
}
