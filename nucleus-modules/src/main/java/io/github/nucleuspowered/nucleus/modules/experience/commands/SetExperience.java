/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.experience.commands;

import io.github.nucleuspowered.nucleus.modules.experience.ExperiencePermissions;
import io.github.nucleuspowered.nucleus.modules.experience.parameter.ExperienceLevelArgument;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.PositiveIntegerArgument;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import java.util.Optional;

@Command(aliases = "set", parentCommand = ExperienceCommand.class,
        basePermission = ExperiencePermissions.BASE_EXP_SET, commandDescriptionKey = "exp.set")
public class SetExperience implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.OPTIONAL_ONE_PLAYER.get(serviceCollection),
                GenericArguments.firstParsing(
                        GenericArguments.onlyOne(new ExperienceLevelArgument(Text.of(ExperienceCommand.levelKey), serviceCollection)),
                        GenericArguments.onlyOne(new PositiveIntegerArgument(Text.of(ExperienceCommand.experienceKey), serviceCollection))
                )
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player pl = context.getPlayerFromArgs();
        final Optional<ICommandResult> r = ExperienceCommand.checkGameMode(context, pl);
        if (r.isPresent()) {
            return r.get();
        }

        final Optional<Integer> l = context.getOne(ExperienceCommand.levelKey, int.class);
        final DataTransactionResult dtr;
        dtr = l.map(integer -> pl.offer(Keys.EXPERIENCE_LEVEL, integer))
                .orElseGet(() -> pl.offer(Keys.TOTAL_EXPERIENCE, context.requireOne(ExperienceCommand.experienceKey, int.class)));

        return ExperienceCommand.tellUserAboutExperience(context, pl, dtr.isSuccessful());
    }
}
