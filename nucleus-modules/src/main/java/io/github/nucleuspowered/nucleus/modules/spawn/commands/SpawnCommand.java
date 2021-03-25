/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.spawn.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportResult;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanners;
import io.github.nucleuspowered.nucleus.modules.spawn.SpawnKeys;
import io.github.nucleuspowered.nucleus.modules.spawn.SpawnPermissions;
import io.github.nucleuspowered.nucleus.modules.spawn.config.GlobalSpawnConfig;
import io.github.nucleuspowered.nucleus.modules.spawn.config.SpawnConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;

@EssentialsEquivalent("spawn")
@Command(
        aliases = "spawn",
        basePermission = SpawnPermissions.BASE_SPAWN,
        commandDescriptionKey = "spawn",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = SpawnPermissions.EXEMPT_COOLDOWN_SPAWN),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = SpawnPermissions.EXEMPT_WARMUP_SPAWN),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = SpawnPermissions.EXEMPT_COST_SPAWN)
        },
        associatedPermissions = {
                SpawnPermissions.SPAWN_FORCE,
                SpawnPermissions.SPAWN_OTHERWORLDS,
                SpawnPermissions.SPAWN_WORLDS
        }
)
public class SpawnCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final Parameter.Value<WorldProperties> worldPropertiesValueParameter;

    private SpawnConfig sc = new SpawnConfig();

    @Inject
    public SpawnCommand(final IPermissionService permissionService) {
        this.worldPropertiesValueParameter = Parameter.worldProperties(true)
                .key("world")
                .optional()
                .requirements(cause -> permissionService.hasPermission(cause, SpawnPermissions.SPAWN_OTHERWORLDS))
                .build();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.sc = serviceCollection.configProvider().getModuleConfig(SpawnConfig.class);
    }

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.builder()
                        .setRequirement(cause -> serviceCollection.permissionService().hasPermission(cause, SpawnPermissions.SPAWN_FORCE))
                        .alias("f")
                        .alias("force")
                        .build()
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            this.worldPropertiesValueParameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer src = context.requirePlayer();
        final boolean force = context.hasFlag("f");
        final GlobalSpawnConfig gsc = this.sc.getGlobalSpawn();
        final WorldProperties wp = context.getOne(this.worldPropertiesValueParameter)
            .orElseGet(() -> {
                if (gsc.isOnSpawnCommand()) {
                    return gsc.getWorld().orElseGet(() -> src.getWorld().getProperties());
                } else {
                    return src.getWorld().getProperties();
                }
            });

        final Optional<ServerWorld> ow = Sponge.server().worldManager().getWorld(wp.getKey());
        if (!ow.isPresent()) {
            return context.errorResult("command.spawn.noworld");
        } else if (this.sc.isPerWorldPerms() &&
                !context.testPermission(SpawnPermissions.SPAWN_WORLDS + "." + ow.get().getKey().asString().toLowerCase())) {
            return context.errorResult("command.spawn.nopermsworld", ow.get().getKey().asString().toLowerCase());
        }

        final ServerWorld targetWorld = ow.get();
        final Vector3d rotation = context.getServiceCollection().storageManager()
                .getOrCreateWorldOnThread(ow.get().getKey())
                .get(SpawnKeys.WORLD_SPAWN_ROTATION)
                .orElseGet(src::getRotation);

        // If we don't have a rotation, then use the current rotation
        final TeleportResult result = context
                .getServiceCollection()
                .teleportService()
                .teleportPlayerSmart(
                        src,
                        ServerLocation.of(targetWorld, targetWorld.getProperties().getSpawnPosition()),
                        rotation,
                        true,
                        !force && this.sc.isSafeTeleport(),
                        TeleportScanners.NO_SCAN.get()
                );

        if (result.isSuccessful()) {
            context.sendMessage("command.spawn.success", wp.getKey().asString());
            return context.successResult();
        }

        if (result == TeleportResult.FAIL_NO_LOCATION) {
            return context.errorResult("command.spawn.fail", wp.getKey().asString());
        }

        return context.errorResult("command.spawn.cancelled", wp.getKey().asString());
    }
}
