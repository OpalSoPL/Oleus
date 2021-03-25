/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.experience.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.modules.experience.ExperiencePermissions;
import io.github.nucleuspowered.nucleus.modules.experience.parameter.ExperienceLevelArgument;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import java.util.Optional;

@Command(
        aliases = "give",
        parentCommand = ExperienceCommand.class,
        basePermission = ExperiencePermissions.BASE_EXP_GIVE,
        commandDescriptionKey = "exp.give"
)
public class GiveExperience implements ICommandExecutor {

    private final Parameter.Value<Integer> experienceLevelParameter;
    private final Parameter.Value<Integer> experienceValueParameter;

    @Inject
    public GiveExperience(final INucleusServiceCollection serviceCollection) {
        this.experienceLevelParameter =
                Parameter.integerNumber().addParser(new ExperienceLevelArgument(serviceCollection))
                        .key("experience").build();
        this.experienceValueParameter =
                Parameter.integerNumber().addParser(VariableValueParameters.integerRange().min(0).max(Integer.MAX_VALUE).build())
                        .key("experience").build();
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_ONE_PLAYER,
                Parameter.firstOf(this.experienceLevelParameter, this.experienceValueParameter)
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player pl = context.getPlayerFromArgs();
        final Optional<ICommandResult> res = ExperienceCommand.checkGameMode(context, pl);
        if (res.isPresent()) {
            return res.get();
        }

        final int extra;
        if (context.hasAny(this.experienceLevelParameter)) {
            final int lvl = pl.get(Keys.EXPERIENCE_LEVEL).orElse(0) + context.requireOne(this.experienceLevelParameter);
            extra = pl.get(Keys.EXPERIENCE_SINCE_LEVEL).orElse(0);

            // Offer level, then we offer the extra experience.
            pl.tryOffer(Keys.EXPERIENCE_LEVEL, lvl);
        } else {
            extra = context.requireOne(this.experienceValueParameter);
        }

        final int exp = pl.get(Keys.EXPERIENCE).orElse(0) + extra;
        return ExperienceCommand.tellUserAboutExperience(context, pl, pl.offer(Keys.EXPERIENCE_LEVEL, exp).isSuccessful());
    }
}
