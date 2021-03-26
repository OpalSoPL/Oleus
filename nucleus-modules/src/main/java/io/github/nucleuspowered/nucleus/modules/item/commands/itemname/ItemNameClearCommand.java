/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands.itemname;

import io.github.nucleuspowered.nucleus.modules.item.ItemPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

@Command(
        aliases = { "clear", "#clearitemname", "#resetitemname" },
        basePermission = ItemPermissions.BASE_ITEMNAME_CLEAR,
        commandDescriptionKey = "itemname.clear",
        parentCommand = ItemNameCommand.class,
        modifiers = {
            @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = ItemPermissions.EXEMPT_COOLDOWN_ITEMNAME_CLEAR),
            @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = ItemPermissions.EXEMPT_WARMUP_ITEMNAME_CLEAR),
            @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = ItemPermissions.EXEMPT_COST_ITEMNAME_CLEAR)
        }
)
public class ItemNameClearCommand implements ICommandExecutor {

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer src = context.getIfPlayer();
        if (src.itemInHand(HandTypes.MAIN_HAND).isEmpty()) {
            return context.errorResult("command.itemname.clear.noitem");
        }

        final ItemStack stack = src.itemInHand(HandTypes.MAIN_HAND);
        final Optional<Component> data = stack.get(Keys.CUSTOM_NAME);

        if (!data.isPresent()) {
            // No display name.
            return context.errorResult("command.lore.clear.none");
        }

        if (stack.remove(Keys.CUSTOM_NAME).isSuccessful()) {
            src.setItemInHand(HandTypes.MAIN_HAND, stack);
            context.sendMessage("command.itemname.clear.success");
            return context.successResult();
        }

        return context.errorResult("command.itemname.clear.fail");
    }
}
