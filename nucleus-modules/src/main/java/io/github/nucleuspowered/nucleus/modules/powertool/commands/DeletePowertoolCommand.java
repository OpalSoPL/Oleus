/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.commands;

import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolPermissions;
import io.github.nucleuspowered.nucleus.modules.powertool.services.PowertoolService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import java.util.UUID;

@Command(
        aliases = {"delete", "del", "rm", "remove"},
        basePermission = PowertoolPermissions.BASE_POWERTOOL,
        commandDescriptionKey = "powertool.delete",
        parentCommand = PowertoolCommand.class
)
public class DeletePowertoolCommand implements ICommandExecutor {

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer player = context.requirePlayer();
        final ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND);
        if (itemStack.isEmpty()) {
            return context.errorResult("command.powertool.noitem");
        }

        final ItemType type = itemStack.getType();
        final UUID uuid = context.uniqueId().get();
        final PowertoolService service = context.getServiceCollection().getServiceUnchecked(PowertoolService.class);
        service.getPowertoolForItem(uuid, type)
                .orElseThrow(() -> context.createException("command.powertool.nocmds", itemStack.getType().asComponent()));
        service.clearPowertool(uuid, type);
        context.sendMessage("command.powertool.removed", itemStack.getType().asComponent());
        return context.successResult();
    }
}
