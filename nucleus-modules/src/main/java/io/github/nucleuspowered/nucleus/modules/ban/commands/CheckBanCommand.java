/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ban.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.ban.BanPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.Ban;
import org.spongepowered.api.service.ban.BanService;

import java.util.Optional;

@Command(aliases = "checkban", basePermission = BanPermissions.BASE_CHECKBAN, commandDescriptionKey = "checkban")
public class CheckBanCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.GAME_PROFILE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final GameProfile profile = context.requireOne(NucleusParameters.GAME_PROFILE);
        final BanService service = Sponge.getServer().getServiceProvider().banService();

        final Optional<Ban.Profile> obp = service.getBanFor(profile);
        if (!obp.isPresent()) {
            return context.errorResult("command.checkban.notset", Util.getNameOrUnkown(context, profile));
        }

        final Ban.Profile bp = obp.get();

        final Component name;
        if (bp.getBanSource().isPresent()) {
            name = bp.getBanSource().get();
        } else {
            name = context.getMessage("standard.unknown");
        }

        if (bp.getExpirationDate().isPresent()) {
            context.sendMessage("command.checkban.bannedfor", Util.getNameOrUnkown(context, profile), name,
                    context.getTimeToNowString(bp.getExpirationDate().get()));
        } else {
            context.sendMessage("command.checkban.bannedperm", Util.getNameOrUnkown(context, profile), name);
        }

        context.sendMessage("command.checkban.created", Util.FULL_TIME_FORMATTER.withLocale(context.getLocale())
                .format(bp.getCreationDate()
        ));
        context.sendMessage("standard.reasoncoloured", LegacyComponentSerializer.legacyAmpersand().serialize(bp.getReason()
                        .orElse(
                        context.getServiceCollection().messageProvider().getMessageFor(context.getAudience(), "ban.defaultreason"))));
        return context.successResult();
    }

}
