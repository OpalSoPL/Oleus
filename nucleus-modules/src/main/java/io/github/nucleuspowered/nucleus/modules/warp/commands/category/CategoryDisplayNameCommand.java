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
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

@Command(
        aliases = "setdisplayname",
        basePermission = WarpPermissions.BASE_CATEGORY_DISPLAYNAME,
        commandDescriptionKey = "warp.category.setdisplayname",
        parentCommand = CategoryCommand.class)
public class CategoryDisplayNameCommand implements ICommandExecutor {

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(WarpService.class).warpCategoryElement(),
                NucleusParameters.DISPLAY_NAME_COMPONENT
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WarpCategory category = context.requireOne(context.getServiceCollection().getServiceUnchecked(WarpService.class).warpCategoryElement());
        final Component displayName = context.requireOne(NucleusParameters.DISPLAY_NAME_COMPONENT);
        context.getServiceCollection()
                .getServiceUnchecked(WarpService.class)
                .setWarpCategoryDisplayName(
                        category.getId(),
                        displayName);
        context.sendMessage("command.warp.category.displayname.set", category.getId(), displayName);
        return context.successResult();
    }
}
