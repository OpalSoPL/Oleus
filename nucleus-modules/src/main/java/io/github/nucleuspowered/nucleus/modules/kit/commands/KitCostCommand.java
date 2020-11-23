/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;

/**
 * Sets kit cost.
 */
@Command(
        aliases = { "cost", "setcost" },
        basePermission = KitPermissions.BASE_KIT_COST,
        commandDescriptionKey = "kit.cost",
        parentCommand = KitCommand.class)
public class KitCostCommand implements ICommandExecutor {

    private final Parameter.Value<Double> costParameter = Parameter.builder(Double.class)
            .parser(VariableValueParameters.doubleRange().setMin(0.0).build())
            .setKey("cost")
            .build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(KitService.class).kitParameterWithoutPermission(),
                this.costParameter
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Kit kit = context.requireOne(KitService.KIT_KEY);
        double cost = context.requireOne(this.costParameter);

        if (cost < 0) {
            cost = 0;
        }

        kit.setCost(cost);
        context.getServiceCollection().getServiceUnchecked(KitService.class).saveKit(kit);
        context.sendMessage("command.kit.cost.success", kit.getName(), String.valueOf(cost));
        return context.successResult();
    }
}
