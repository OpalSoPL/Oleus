/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands;

import io.github.nucleuspowered.nucleus.modules.item.ItemPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ItemStack;

@Command(
        aliases = {"showitemattributes", "showattributes"},
        basePermission = ItemPermissions.BASE_SHOWITEMATTRIBUTES,
        commandDescriptionKey = "showitemattributes",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = ItemPermissions.EXEMPT_COOLDOWN_SHOWITEMATTRIBUTES),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = ItemPermissions.EXEMPT_WARMUP_SHOWITEMATTRIBUTES),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = ItemPermissions.EXEMPT_COST_SHOWITEMATTRIBUTES)
        }
)
public class ShowAttributesCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer src = context.getIfPlayer();
        final ItemStack itemStack = src.getItemInHand(HandTypes.MAIN_HAND);
        if (itemStack.isEmpty()) {
            return context.errorResult("command.generalerror.handempty");
        }

        final boolean b = context.getOne(NucleusParameters.OPTIONAL_ONE_TRUE_FALSE)
                .orElseGet(() -> itemStack.get(Keys.HIDE_ATTRIBUTES).orElse(false));

        // Command is show, key is hide. We invert.
        itemStack.offer(Keys.HIDE_ATTRIBUTES, !b);
        src.setItemInHand(HandTypes.MAIN_HAND, itemStack);

        context.sendMessage("command.showitemattributes.success." + b, itemStack.getType().asComponent());
        return context.successResult();
    }

}
