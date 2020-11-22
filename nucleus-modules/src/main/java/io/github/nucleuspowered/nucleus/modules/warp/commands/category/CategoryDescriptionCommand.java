/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.commands.category;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.module.warp.data.WarpCategory;
import io.github.nucleuspowered.nucleus.modules.warp.WarpPermissions;
import io.github.nucleuspowered.nucleus.modules.warp.services.WarpService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

@Command(
        aliases = "setdescription",
        basePermission = WarpPermissions.BASE_CATEGORY_DESCRIPTION,
        commandDescriptionKey = "warp.category.setdescription",
        parentCommand = CategoryCommand.class
)
public class CategoryDescriptionCommand implements ICommandExecutor {

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(WarpService.class).warpCategoryElement(),
                NucleusParameters.DESCRIPTION
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WarpCategory category = context.requireOne(context.getServiceCollection().getServiceUnchecked(WarpService.class).warpCategoryElement());
        final String d = context.requireOne(NucleusParameters.DESCRIPTION);
        context.getServiceCollection().getServiceUnchecked(WarpService.class)
                .setWarpCategoryDescription(category.getId(), LegacyComponentSerializer.legacySection().deserialize(d));
        context.sendMessage("command.warp.category.description.set", category.getId(), d);
        return context.successResult();
    }
}
