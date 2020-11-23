/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;

@EssentialsEquivalent(value = "world", notes = "The world command in Essentials was just a warp command.")
@Command(
        aliases = {"teleport", "tp"},
        basePermission = WorldPermissions.BASE_WORLD_TELEPORT,
        commandDescriptionKey = "world.teleport",
        parentCommand = WorldCommand.class,
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = WorldPermissions.EXEMPT_COOLDOWN_WORLD_TELEPORT),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = WorldPermissions.EXEMPT_WARMUP_WORLD_TELEPORT),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = WorldPermissions.EXEMPT_COST_WORLD_TELEPORT)
        },
        associatedPermissions = {
                WorldPermissions.WORLD_TELEPORT_OTHER,
                WorldPermissions.WORLDS_ACCESS_PERMISSION_PREFIX
        }
)
public class TeleportWorldCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            NucleusParameters.WORLD_PROPERTIES_LOADED_ONLY,
            serviceCollection.commandElementSupplier()
                .createOnlyOtherPlayerPermissionElement(WorldPermissions.WORLD_TELEPORT_OTHER)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer player = context.getIfPlayer("command.world.player");
        final WorldProperties worldProperties = context.requireOne(NucleusParameters.WORLD_PROPERTIES_LOADED_ONLY);
        final Vector3d pos = worldProperties.getSpawnPosition().toDouble();
        if (!worldProperties.getWorld().map(x -> player.transferToWorld(x, pos)).filter(x -> x).isPresent()) {
            return context.errorResult("command.world.teleport.failed", worldProperties.getKey().asString());
        }

        if (context.is(player)) {
            context.sendMessage("command.world.teleport.success", worldProperties.getKey().asString());
        } else {
            context.sendMessage("command.world.teleport.successplayer",
                    context.getServiceCollection().playerDisplayNameService().getDisplayName(player),
                    worldProperties.getKey().asString());
            context.sendMessageTo(player, "command.world.teleport.success", worldProperties.getKey().asString());
        }

        return context.successResult();
    }
}
