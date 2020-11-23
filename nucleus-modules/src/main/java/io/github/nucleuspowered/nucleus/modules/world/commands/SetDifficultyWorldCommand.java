/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;

@Command(
        aliases = {"setdifficulty", "difficulty"},
        basePermission = WorldPermissions.BASE_WORLD_SETDIFFICULTY,
        commandDescriptionKey = "world.setdifficulty",
        parentCommand = WorldCommand.class
)
public class SetDifficultyWorldCommand implements ICommandExecutor {

    private final Parameter.Value<Difficulty> difficultyValue = Parameter.catalogedElement(Difficulty.class).setKey("difficulty").build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.difficultyValue,
                NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ALL
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Difficulty difficultyInput = context.requireOne(this.difficultyValue);
        final WorldProperties worldProperties = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ALL.getKey())
                        .orElseThrow(() -> context.createException("command.world.player"));

        worldProperties.setDifficulty(difficultyInput);
        context.sendMessage("command.world.setdifficulty.success",
                worldProperties.getKey().asString(),
                difficultyInput.asComponent());

        return context.successResult();
    }
}
