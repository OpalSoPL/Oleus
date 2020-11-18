/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.mute.MutePermissions;
import io.github.nucleuspowered.nucleus.modules.mute.data.MuteData;
import io.github.nucleuspowered.nucleus.modules.mute.services.MuteHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import java.time.Instant;
import java.util.Optional;

@Command( aliases = "checkmute", basePermission = MutePermissions.BASE_CHECKMUTE, commandDescriptionKey = "checkmute")
public class CheckMuteCommand implements ICommandExecutor {

    private final String playerKey = "user/UUID";

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            GenericArguments.firstParsing(
                GenericArguments.user(Text.of(this.playerKey)),
                    new UUIDArgument<>(Text.of(this.playerKey), u -> Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(u),
                            serviceCollection)
            )
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Get the user.
        final User user = context.requireOne(this.playerKey, User.class);
        final MuteHandler muteHandler = context.getServiceCollection().getServiceUnchecked(MuteHandler.class);

        final Optional<MuteData> omd = muteHandler.getPlayerMuteData(user);
        if (!omd.isPresent()) {
            context.sendMessage("command.checkmute.none", user.getName());
            return context.successResult();
        }

        // Muted, get information.
        final MuteData md = omd.get();
        final String name;
        if (!md.getMuter().isPresent()) {
            name = Sponge.getServer().getConsole().getName();
        } else {
            name = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(md.getMuter().get())
                    .map(User::getName)
                    .orElseGet(() -> context.getMessageString("standard.unknown"));
        }

        if (md.getRemainingTime().isPresent()) {
            context.sendMessage("command.checkmute.mutedfor", user.getName(),
                    name,
                    context.getTimeString(md.getRemainingTime().get().getSeconds()));
        } else {
            context.sendMessage("command.checkmute.mutedperm", user.getName(), name);
        }

        if (md.getCreationTime() > 0) {
            context.sendMessage("command.checkmute.created",
                    Util.FULL_TIME_FORMATTER.withLocale(context.getCommandSourceRoot().getLocale())
                            .format(Instant.ofEpochSecond(md.getCreationTime())));
        } else {
            context.sendMessage("command.checkmute.created", "loc:standard.unknown");
        }

        context.sendMessage("standard.reasoncoloured", md.getReason());
        return context.successResult();
    }
}
