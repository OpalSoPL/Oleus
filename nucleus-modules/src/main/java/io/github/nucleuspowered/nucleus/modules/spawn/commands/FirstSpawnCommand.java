/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.commands;

import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportResult;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanners;
import io.github.nucleuspowered.nucleus.core.configurate.datatypes.LocationNode;
import io.github.nucleuspowered.nucleus.modules.spawn.SpawnKeys;
import io.github.nucleuspowered.nucleus.modules.spawn.SpawnPermissions;
import io.github.nucleuspowered.nucleus.modules.spawn.config.SpawnConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;

@Command(
        aliases = "firstspawn",
        basePermission = SpawnPermissions.BASE_FIRSTSPAWN,
        commandDescriptionKey = "firstspawn",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = SpawnPermissions.EXEMPT_COOLDOWN_FIRSTSPAWN),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = SpawnPermissions.EXEMPT_WARMUP_FIRSTSPAWN),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = SpawnPermissions.EXEMPT_COST_FIRSTSPAWN)
        }
)
public class FirstSpawnCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private boolean isSafeTeleport = true;

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {

        final Optional<LocationNode> olwr =
                context.getServiceCollection().storageManager()
                        .getGeneralService()
                        .getOrNewOnThread()
                        .get(SpawnKeys.FIRST_SPAWN_LOCATION);
        if (!olwr.isPresent()) {
            return context.errorResult("command.firstspawn.notset");
        }

        final Optional<ServerLocation> serverLocation = olwr.get().getLocationIfExists();
        if (!serverLocation.isPresent()) {
            return context.errorResult("command.firstspawn.notloaded", olwr.get().getWorld().asString());
        }

        final TeleportResult result = context.getServiceCollection()
                .teleportService()
                .teleportPlayerSmart(
                        context.getIfPlayer(),
                        serverLocation.get(),
                        Vector3d.ZERO,
                        true,
                        this.isSafeTeleport,
                        TeleportScanners.NO_SCAN.get()
                );
        if (result.isSuccessful()) {
            context.sendMessage("command.firstspawn.success");
            return context.successResult();
        }

        return context.errorResult("command.firstspawn.fail");
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.isSafeTeleport = serviceCollection.configProvider().getModuleConfig(SpawnConfig.class).isSafeTeleport();
    }
}
