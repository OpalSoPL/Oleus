/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands;

import io.github.nucleuspowered.nucleus.modules.item.ItemPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

@EssentialsEquivalent("more")
@Command(
        aliases = { "more", "stack" },
        basePermission = ItemPermissions.BASE_MORE,
        commandDescriptionKey = "more",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = ItemPermissions.EXEMPT_COOLDOWN_MORE),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = ItemPermissions.EXEMPT_WARMUP_MORE),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = ItemPermissions.EXEMPT_COST_MORE)
        }
)
public class MoreCommand implements ICommandExecutor {

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player player = context.getIfPlayer();
        if (!player.getItemInHand(HandTypes.MAIN_HAND).isEmpty()) {
            final ItemStack stack = player.getItemInHand(HandTypes.MAIN_HAND);
            stack.setQuantity(stack.getMaxStackQuantity());
            player.setItemInHand(HandTypes.MAIN_HAND, stack);
            context.sendMessage("command.more.success", stack.type().asComponent(), stack.type().getMaxStackQuantity());
            return context.successResult();
        }

        return context.errorResult("command.more.none");
    }
}
