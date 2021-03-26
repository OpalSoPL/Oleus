/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.properties;

import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;

abstract class AbstractPropertiesSetCommand implements ICommandExecutor {

    private final String name;

    AbstractPropertiesSetCommand(final String name) {
        this.name = name;
    }

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONLINE_WORLD_OPTIONAL,
                NucleusParameters.ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerWorld worldProperties = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.ONLINE_WORLD_OPTIONAL)
                .orElseThrow(() -> context.createException("command.world.player"));
        final boolean set = context.requireOne(NucleusParameters.ONE_TRUE_FALSE);
        this.setter(worldProperties.properties(), set);
        context.sendMessage("command.world.setproperty.success", this.name, worldProperties.key().asString(), String.valueOf(set));
        this.extraLogic(context, worldProperties.properties(), set);
        return context.successResult();
    }

    protected abstract void setter(ServerWorldProperties worldProperties, boolean set) throws CommandException;

    protected void extraLogic(final ICommandContext context, final ServerWorldProperties worldProperties, final boolean set) throws CommandException {
        // noop
    }

}
