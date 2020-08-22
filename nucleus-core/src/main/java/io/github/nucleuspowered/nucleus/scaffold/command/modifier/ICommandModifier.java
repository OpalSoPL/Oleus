/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.modifier;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.config.CommandModifiersConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public interface ICommandModifier extends CatalogType {

    /**
     * Validates the information returned in the annotation.
     *
     * @throws IllegalArgumentException if the annotation could not be validated.
     */
    default void validate(final CommandModifier annotation) throws IllegalArgumentException { }

    default void getDefaultNode(final ConfigurationNode node, final IMessageProviderService messageProviderService) { }

    default void setDataFromNode(final CommandModifiersConfig config, final ConfigurationNode node) { }

    default void setValueFromOther(final CommandModifiersConfig from, final CommandModifiersConfig to) { }

    /**
     * Returns whether this can execute and therefore modify the command.
     *
     * @return if so.
     */
    default boolean canExecuteModifier(final INucleusServiceCollection serviceCollection, final CommandSource source) throws CommandException {
        return true;
    }

    /**
     * Tests to see if the state fulfills this requirement.
     *
     * <p>This will return an empty optional if the requirement is met, or
     * a {@link Text} object otherwise, explaining the problem.</p>
     */
    default Optional<Text> testRequirement(final ICommandContext.Mutable<? extends CommandSource> source,
            final CommandControl control,
            final INucleusServiceCollection serviceCollection,
            final CommandModifier modifier) throws CommandException {
        return Optional.empty();
    }

    /**
     * Defines whether a command should continue after pre-execute.
     *
     * @param source The source
     * @param control The {@link CommandControl}
     * @param serviceCollection The {@link INucleusServiceCollection}
     * @param modifier The {@link CommandModifier} annotation associated with this
     * @return a success if everything is OK but we need to stop, a fail if we're stopping, empty to continue.
     */
    default Optional<ICommandResult> preExecute(
            final ICommandContext.Mutable<? extends CommandSource> source,
            final CommandControl control,
            final INucleusServiceCollection serviceCollection,
            final CommandModifier modifier) {
        return Optional.empty();
    }

    default void onCompletion(
            final ICommandContext<? extends CommandSource> source,
            final CommandControl control,
            final INucleusServiceCollection serviceCollection,
            final CommandModifier modifier) throws CommandException {
    }

    default void onFailure(
            final ICommandContext<? extends CommandSource> source,
            final CommandControl control,
            final INucleusServiceCollection serviceCollection,
            final CommandModifier modifier) throws CommandException {

    }

}
