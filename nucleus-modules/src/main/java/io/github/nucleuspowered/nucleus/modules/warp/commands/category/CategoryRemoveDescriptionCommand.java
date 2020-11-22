/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.commands.category;

import io.github.nucleuspowered.nucleus.api.module.warp.data.WarpCategory;
import io.github.nucleuspowered.nucleus.modules.warp.WarpPermissions;
import io.github.nucleuspowered.nucleus.modules.warp.services.WarpService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

@Command(
        aliases = "removedescription",
        basePermission = WarpPermissions.BASE_CATEGORY_DESCRIPTION,
        commandDescriptionKey = "warp.category.removedescription",
        parentCommand = CategoryCommand.class)
public class CategoryRemoveDescriptionCommand implements ICommandExecutor {

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(WarpService.class).warpCategoryElement()
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WarpCategory category = context.requireOne(context.getServiceCollection().getServiceUnchecked(WarpService.class).warpCategoryElement());
        context.getServiceCollection()
                .getServiceUnchecked(WarpService.class)
                .setWarpCategoryDescription(category.getId(), null);
        context.sendMessage("command.warp.category.description.removed", category.getId());
        return context.successResult();
    }
}
