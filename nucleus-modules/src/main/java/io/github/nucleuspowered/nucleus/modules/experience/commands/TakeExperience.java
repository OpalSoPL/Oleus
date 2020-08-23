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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import java.util.Optional;

@Command(aliases = "take",
        parentCommand = ExperienceCommand.class,
        basePermission = ExperiencePermissions.BASE_EXP_TAKE,
        commandDescriptionKey = "exp.take")
public class TakeExperience implements ICommandExecutor {

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

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player pl = context.getPlayerFromArgs();
        final Optional<ICommandResult> r = ExperienceCommand.checkGameMode(context, pl);
        if (r.isPresent()) {
            return r.get();
        }

        // int currentExp = pl.get(Keys.TOTAL_EXPERIENCE).get();
        int toOffer = 0;
        if (context.hasAny(ExperienceCommand.levelKey)) {
            final ExperienceHolderData data = pl.get(ExperienceHolderData.class).get();
            final int currentLevel = data.level().get();
            final int levelReduction = context.requireOne(ExperienceCommand.levelKey, int.class);

            // If this will take us down to below zero, we just let this continue to the return line. Else...
            if (currentLevel >= levelReduction) {
                final int extra = data.experienceSinceLevel().get();
                data.set(data.level().set(currentLevel - levelReduction));
                data.set(data.experienceSinceLevel().set(Math.min(extra, data.getExperienceBetweenLevels().getMaxValue())));
                return ExperienceCommand.tellUserAboutExperience(context, pl, pl.offer(data).isSuccessful());
            }
        } else {
            toOffer = pl.get(Keys.TOTAL_EXPERIENCE).get() - context.requireOne(ExperienceCommand.experienceKey, int.class);
        }

        return ExperienceCommand.tellUserAboutExperience(context, pl, pl.offer(Keys.TOTAL_EXPERIENCE, Math.max(0, toOffer)).isSuccessful());
    }
}
