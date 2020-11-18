/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warp.commands;

import io.github.nucleuspowered.nucleus.api.module.warp.data.Warp;
import io.github.nucleuspowered.nucleus.modules.warp.WarpPermissions;
import io.github.nucleuspowered.nucleus.modules.warp.services.WarpService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
@Command(
        aliases = {"setdescription"},
        basePermission = WarpPermissions.BASE_WARP_SETDESCRIPTION,
        commandDescriptionKey = "warp.setdescription",
        parentCommand = WarpCommand.class
)
public class SetDescriptionCommand implements ICommandExecutor {

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            GenericArguments.flags().flag("r", "-remove", "-delete").buildWith(
                GenericArguments.seq(
                        serviceCollection.getServiceUnchecked(WarpService.class).warpElement(false),
                        NucleusParameters.OPTIONAL_DESCRIPTION
                )
            )
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WarpService handler = context.getServiceCollection().getServiceUnchecked(WarpService.class);
        final String warpName = context.requireOne(WarpService.WARP_KEY, Warp.class).getName();
        if (context.hasAny("r")) {
            // Remove the desc.
            if (handler.setWarpDescription(warpName, null)) {
                context.sendMessage("command.warp.description.removed", warpName);
                return context.successResult();
            }

            return context.errorResult("command.warp.description.noremove", warpName);
        }

        // Add the category.
        final TextComponent message = TextSerializers.FORMATTING_CODE.deserialize(context.requireOne(NucleusParameters.Keys.DESCRIPTION, String.class));
        if (handler.setWarpDescription(warpName, message)) {
            context.sendMessage("command.warp.description.added", message, Text.of(warpName));
            return context.successResult();
        }

        return context.errorResult("command.warp.description.couldnotadd", Text.of(warpName));
    }
}
