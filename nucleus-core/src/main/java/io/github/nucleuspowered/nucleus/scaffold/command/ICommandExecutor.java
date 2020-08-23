/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command;

import io.github.nucleuspowered.nucleus.Constants;
import io.github.nucleuspowered.nucleus.annotationprocessor.Store;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;

import java.util.Optional;

/**
 * Defines the logic of the command.
 *
 * <p>Note that the command must also be annotated with {@link Command}</p>
 *
 * @param <C> The type of {@link Object} that can run this command.
 */
@Store(Constants.COMMAND)
public interface ICommandExecutor {

    /**
     * The flags that make up the command
     *
     * @param serviceCollection The {@link INucleusServiceCollection} to use
     * @return The flags.
     */
    default Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[0];
    }

    /**
     * The elements that make up the command.
     *
     * @param serviceCollection The {@link INucleusServiceCollection} to use
     * @return The elements.
     */
    default Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[0];
    }

    /**
     * Executes before things like warmups execute. If a result is returned,
     * the command will not execute.
     *
     * <p>Basically, this will run before {@link #execute(ICommandContext)} and
     * has the ability to cancel it.</p>
     *
     * @param context The {@link ICommandContext}
     * @return The result, if any
     */
    default Optional<ICommandResult> preExecute(final ICommandContext context) throws CommandException {
        return Optional.empty();
    }

    /**
     * The executor of the command.
     *
     * @param context The {@link ICommandContext}
     * @return The result of the command.
     */
    ICommandResult execute(ICommandContext context) throws CommandException;

}
