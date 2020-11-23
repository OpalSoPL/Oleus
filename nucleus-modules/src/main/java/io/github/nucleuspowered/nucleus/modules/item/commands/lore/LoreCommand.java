/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands.lore;

import io.github.nucleuspowered.nucleus.modules.item.ItemPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import org.spongepowered.api.command.exception.CommandException;

@Command(
        aliases = { "lore" },
        basePermission = ItemPermissions.BASE_LORE,
        commandDescriptionKey = "lore",
        hasExecutor = false
)
public class LoreCommand implements ICommandExecutor {

    // Not executed.
    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        return context.failResult(); // no-op
    }
}
