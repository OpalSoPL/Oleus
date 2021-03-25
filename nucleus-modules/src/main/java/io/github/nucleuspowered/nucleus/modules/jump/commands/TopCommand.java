/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jump.commands;

import io.github.nucleuspowered.nucleus.api.teleport.data.NucleusTeleportHelperFilters;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportResult;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanners;
import io.github.nucleuspowered.nucleus.modules.jump.JumpPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.teleport.TeleportHelperFilters;

@Command(
        aliases = {"top", "tosurface", "totop"},
        basePermission = JumpPermissions.BASE_TOP,
        commandDescriptionKey = "top",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = JumpPermissions.EXEMPT_COOLDOWN_TOP),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = JumpPermissions.EXEMPT_WARMUP_TOP),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = JumpPermissions.EXEMPT_COST_TOP)
        },
        associatedPermissions = JumpPermissions.OTHERS_TOP
)
public class TopCommand implements ICommandExecutor {

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of("f", "force")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier().createOnlyOtherPlayerPermissionElement(JumpPermissions.OTHERS_TOP)
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer playerToTeleport = context.getPlayerFromArgs();
        final ServerLocation location = playerToTeleport.serverLocation().asHighestLocation().add(0, 1, 0);

        final boolean isSafe = !context.hasFlag("f");
        final TeleportResult result = context.getServiceCollection()
                .teleportService()
                .teleportPlayer(
                        playerToTeleport,
                        location,
                        playerToTeleport.getRotation(),
                        false,
                        TeleportScanners.NO_SCAN.get(),
                        isSafe ? TeleportHelperFilters.SURFACE_ONLY.get() : NucleusTeleportHelperFilters.NO_CHECK.get()
                );

        if (result.isSuccessful()) {
            // OK
            if (!context.is(playerToTeleport)) {
                context.sendMessage("command.top.success.other", context.getDisplayName(playerToTeleport.uniqueId()));
            }

            context.sendMessageTo(playerToTeleport, "command.top.success.self");
            return context.successResult();
        }

        if (result == TeleportResult.FAIL_NO_LOCATION) {
            return context.errorResult("command.top.notsafe");
        } else {
            return context.errorResult("command.top.cancelled");
        }
    }
}
