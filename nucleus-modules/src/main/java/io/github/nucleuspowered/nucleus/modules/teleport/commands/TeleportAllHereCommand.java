/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanners;
import io.github.nucleuspowered.nucleus.modules.teleport.TeleportPermissions;
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
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

@EssentialsEquivalent("tpall")
@Command(aliases = {"tpall", "tpallhere"}, basePermission = TeleportPermissions.BASE_TPALL, commandDescriptionKey = "tpall")
public class TeleportAllHereCommand implements ICommandExecutor {

    @Override public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of("f", "force")
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer serverPlayer = context.requirePlayer();
        final ServerLocation toLocation = serverPlayer.getServerLocation();
        final Vector3d toRotation = serverPlayer.getRotation();
        context.sendMessageTo(Sponge.server(), "command.tpall.broadcast", context.getName());
        Sponge.server().getOnlinePlayers().forEach(x -> {
            if (!context.is(x)) {
                context.getServiceCollection()
                        .teleportService()
                        .teleportPlayerSmart(x,
                                toLocation,
                                toRotation, false,
                                !context.hasFlag("f"),
                                TeleportScanners.NO_SCAN.get()
                );
            }
        });

        return context.successResult();
    }
}
