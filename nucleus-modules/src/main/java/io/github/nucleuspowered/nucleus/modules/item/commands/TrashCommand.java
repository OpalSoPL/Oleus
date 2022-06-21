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
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

@Command(
        aliases = {"trash"},
        basePermission = ItemPermissions.BASE_TRASH,
        commandDescriptionKey = "trash",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = ItemPermissions.EXEMPT_COOLDOWN_TRASH),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = ItemPermissions.EXEMPT_WARMUP_TRASH),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = ItemPermissions.EXEMPT_COST_TRASH)
        }
)
public class TrashCommand implements ICommandExecutor {

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer src = context.getIfPlayer();
        if (src.openInventory(ViewableInventory.builder()
                .type(ContainerTypes.GENERIC_9X6)
                .completeStructure()
                .plugin(context.getServiceCollection().pluginContainer())
                .build()).isPresent()) {
            return context.successResult();
        }

        return context.errorResult("command.trash.error");
    }
}
