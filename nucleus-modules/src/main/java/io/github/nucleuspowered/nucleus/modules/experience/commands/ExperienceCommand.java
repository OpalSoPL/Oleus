/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.experience.commands;

import io.github.nucleuspowered.nucleus.modules.experience.ExperiencePermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Optional;

@Command(aliases = {"exp", "experience", "xp"},
        basePermission = ExperiencePermissions.BASE_EXP,
        commandDescriptionKey = "exp")
@EssentialsEquivalent({"exp", "xp"})
public class ExperienceCommand implements ICommandExecutor {

    static final String experienceKey = "experience";
    static final String levelKey = "level";

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_ONE_PLAYER
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer pl = context.getPlayerFromArgs();

        final int exp = pl.get(Keys.EXPERIENCE).orElse(0);
        final int lv = pl.get(Keys.EXPERIENCE_LEVEL).orElse(0);

        context.sendMessage("command.exp.info", pl.getName(), exp, lv);
        return context.successResult();
    }

    static ICommandResult tellUserAboutExperience(final ICommandContext context, final Player pl, final boolean isSuccess) throws CommandException {
        if (!isSuccess) {
            return context.errorResult("command.exp.set.error");
        }

        final int exp = pl.get(Keys.EXPERIENCE).get();
        final int newLvl = pl.get(Keys.EXPERIENCE_LEVEL).get();

        final IMessageProviderService messageProviderService = context.getServiceCollection().messageProvider();
        if (!context.is(pl)) {
            context.sendMessage("command.exp.set.new.other",
                            pl.getName(),
                            exp,
                            newLvl);
        }

        messageProviderService.sendMessageTo(pl, "command.exp.set.new.self", String.valueOf(exp), String.valueOf(newLvl));
        return context.successResult();
    }

    static Optional<ICommandResult> checkGameMode(final ICommandContext source, final Player pl) throws CommandException {
        final GameMode gm = pl.get(Keys.GAME_MODE).orElse(GameModes.SURVIVAL.get());
        if (gm == GameModes.CREATIVE || gm == GameModes.SPECTATOR) {
            return Optional.of(source.errorResult("command.exp.gamemode", pl.getName()));
        }

        return Optional.empty();
    }
}
