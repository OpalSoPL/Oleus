/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.parameters.KitParameter;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
@Command(
        aliases = { "toggleredeemmessage", "togglemessage" },
        async = true,
        basePermission = KitPermissions.BASE_KIT_TOGGLEREDEEMMESSAGE,
        commandDescriptionKey = "kit.toggleredeemmessage",
        parentCommand = KitCommand.class
)
public class KitRedeemMessageCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.getServiceUnchecked(KitService.class).createKitElement(false),
                NucleusParameters.ONE_TRUE_FALSE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Kit kitInfo = context.requireOne(KitParameter.KIT_PARAMETER_KEY, Kit.class);
        final boolean b = context.requireOne(NucleusParameters.Keys.BOOL, Boolean.class);

        // This Kit is a reference back to the version in list, so we don't need
        // to update it explicitly
        kitInfo.setDisplayMessageOnRedeem(b);
        context.getServiceCollection().getServiceUnchecked(KitService.class).saveKit(kitInfo);
        context.sendMessage(b ? "command.kit.displaymessage.on" : "command.kit.displaymessage.off", kitInfo.getName());

        return context.successResult();
    }
}
