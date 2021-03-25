/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.notification.command;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.modules.notification.NotificationPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

@Command(
        aliases = "basicactionbar",
        basePermission = NotificationPermissions.BASE_BASICACTIONBAR,
        commandDescriptionKey = "basicactionbar"
)
public class BasicActionbarCommand implements ICommandExecutor {

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[]{
                Flag.of(NucleusParameters.MANY_PLAYER, "p")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // If we don't have a player, check we can send to all.
        final Collection<ServerPlayer> targets =
                context.getOne(NucleusParameters.MANY_PLAYER)
                        .map(Collections::unmodifiableCollection)
                        .orElseGet(Sponge.server()::onlinePlayers);

        if (targets.isEmpty()) {
            return context.errorResult("command.title.noonline");
        }

        if (targets.size() > 1 && !context.testPermission(NotificationPermissions.BASICACTIONBAR_MULTI)) {
            return context.errorResult("command.title.multi.noperms");
        }

        final String message = context.requireOne(NucleusParameters.MESSAGE);
        final NucleusTextTemplate textTemplate =
                context.getServiceCollection().textTemplateFactory().createFromAmpersandString(message);
        final Object sender = context.getCommandSourceRoot();
        if (targets.size() > 1) {
            for (final ServerPlayer pl : targets) {
                pl.sendActionBar(textTemplate.getForObjectWithSenderToken(pl, sender));
            }
            context.sendMessage("command.title.player.success.multi", "Action Bar", targets.size());
        } else {
            final ServerPlayer pl = targets.iterator().next();
            final Component t = textTemplate.getForObjectWithSenderToken(pl, sender);
            pl.sendActionBar(t);
            context.sendMessage("command.title.player.success.single", "Action Bar", t, pl.name());
        }
        return context.successResult();
    }

}
