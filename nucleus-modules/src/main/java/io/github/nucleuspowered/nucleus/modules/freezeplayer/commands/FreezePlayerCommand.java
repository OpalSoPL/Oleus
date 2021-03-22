/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.freezeplayer.commands;

import io.github.nucleuspowered.nucleus.modules.freezeplayer.FreezePlayerPermissions;
import io.github.nucleuspowered.nucleus.modules.freezeplayer.services.FreezePlayerService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;

@Command(
        aliases = {"freezeplayer", "freeze"},
        basePermission = FreezePlayerPermissions.BASE_FREEZEPLAYER,
        commandDescriptionKey = "freezeplayer",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = FreezePlayerPermissions.EXEMPT_COOLDOWN_FREEZEPLAYER),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission =  FreezePlayerPermissions.EXEMPT_WARMUP_FREEZEPLAYER),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = FreezePlayerPermissions.EXEMPT_COST_FREEZEPLAYER)
        },
        associatedPermissions = FreezePlayerPermissions.OTHERS_FREEZEPLAYER
)
public class FreezePlayerCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier().createOnlyOtherUserPermissionElement(FreezePlayerPermissions.OTHERS_FREEZEPLAYER),
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User pl = context.getUserFromArgs();
        final FreezePlayerService service = context.getServiceCollection().getServiceUnchecked(FreezePlayerService.class);
        final boolean f = context.getOne(NucleusParameters.OPTIONAL_ONE_TRUE_FALSE).orElseGet(() -> !service.isFrozen(pl.uniqueId()));
        service.setFrozen(pl.uniqueId(), f);
        context.sendMessage(
            f ? "command.freezeplayer.success.frozen" : "command.freezeplayer.success.unfrozen",
                context.getServiceCollection().playerDisplayNameService().getDisplayName(pl));
        return context.successResult();
    }
}
