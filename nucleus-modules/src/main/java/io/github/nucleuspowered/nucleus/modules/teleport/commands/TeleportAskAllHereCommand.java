/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import io.github.nucleuspowered.nucleus.modules.teleport.TeleportPermissions;
import io.github.nucleuspowered.nucleus.modules.teleport.events.RequestEvent;
import io.github.nucleuspowered.nucleus.modules.teleport.services.PlayerTeleporterService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.Nameable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EssentialsEquivalent({"tpaall"})
@Command(aliases = {"tpaall", "tpaskall"}, basePermission = TeleportPermissions.BASE_TPAALL, commandDescriptionKey = "tpaall")
public class TeleportAskAllHereCommand implements ICommandExecutor {

    @Override public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of("f", "force")
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final List<ServerPlayer> cancelled = new ArrayList<>();
        final PlayerTeleporterService playerTeleporterService = context
                .getServiceCollection()
                .getServiceUnchecked(PlayerTeleporterService.class);
        for (final ServerPlayer x : Sponge.server().onlinePlayers()) {
            if (context.is(x)) {
                continue;
            }

            // Before we do all this, check the event.
            final RequestEvent.PlayerToCause event = new RequestEvent.PlayerToCause(Sponge.server().causeStackManager().currentCause(), x.uniqueId());
            if (Sponge.eventManager().post(event)) {
                cancelled.add(x);
                continue;
            }

            playerTeleporterService.requestTeleport(
                    context.getIfPlayer(),
                    x,
                    0,
                    0,
                    x,
                    context.getIfPlayer(),
                    !context.hasFlag("f"),
                    false,
                    true,
                    p -> {},
                    "command.tpahere.question"
            );
        }

        context.sendMessage("command.tpaall.success");
        if (!cancelled.isEmpty()) {
            context.sendMessage("command.tpall.cancelled",
                    cancelled.stream().map(Nameable::getName).collect(Collectors.joining(", ")));
        }

        return context.successResult();
    }
}
