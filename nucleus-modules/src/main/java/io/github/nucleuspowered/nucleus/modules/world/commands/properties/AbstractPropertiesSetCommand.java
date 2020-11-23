/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.properties;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.storage.WorldProperties;

abstract class AbstractPropertiesSetCommand implements ICommandExecutor {

    private final String name;

    AbstractPropertiesSetCommand(final String name) {
        this.name = name;
    }

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ALL,
                NucleusParameters.ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties worldProperties = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ALL.getKey())
                .orElseThrow(() -> context.createException("command.world.player"));
        final boolean set = context.requireOne(NucleusParameters.ONE_TRUE_FALSE);
        this.setter(worldProperties, set);
        context.sendMessage("command.world.setproperty.success", this.name, worldProperties.getKey().asString(), String.valueOf(set));
        this.extraLogic(context, worldProperties, set);
        return context.successResult();
    }

    protected abstract void setter(WorldProperties worldProperties, boolean set) throws CommandException;

    protected void extraLogic(final ICommandContext context, final WorldProperties worldProperties, final boolean set) throws CommandException {
        // noop
    }

}
