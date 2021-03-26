/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jump.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanners;
import io.github.nucleuspowered.nucleus.modules.jump.JumpPermissions;
import io.github.nucleuspowered.nucleus.modules.jump.config.JumpConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.util.blockray.RayTraceResult;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.teleport.TeleportHelperFilters;

import java.util.Optional;

@EssentialsEquivalent({"jump", "j", "jumpto"})
@Command(
        aliases = {"jump", "j", "jmp"},
        basePermission = JumpPermissions.BASE_JUMP,
        commandDescriptionKey = "jump",
        modifiers = {
            @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = JumpPermissions.EXEMPT_COOLDOWN_JUMP),
            @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = JumpPermissions.EXEMPT_WARMUP_JUMP),
            @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = JumpPermissions.EXEMPT_COST_JUMP)
        }
)
public class JumpCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private int maxJump = 20;

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer player = context.requirePlayer();
        final Optional<RayTraceResult<LocatableBlock>> blockRayTraceResult = RayTrace.block()
                .sourceEyePosition(player)
                .direction(player.direction())
                .limit(this.maxJump)
                .select(RayTrace.nonAir())
                .continueWhileBlock(RayTrace.onlyAir())
                .execute();

        if (!blockRayTraceResult.isPresent()) {
            // We didn't find anywhere to jump to.
            return context.errorResult("command.jump.noblock");
        }

        final LocatableBlock block = blockRayTraceResult.get().selectedObject();
        final ServerLocation targetLocation;
        if (RayTrace.onlyAir().test(block.serverLocation().add(0, 1, 0).asLocatableBlock()) &&
                        RayTrace.onlyAir().test(block.serverLocation().add(0, 2, 0).asLocatableBlock())) {
            targetLocation = block.serverLocation().add(0, 1, 0);
        } else {
            // safe teleport
            targetLocation = context.getServiceCollection().teleportService()
                    .getSafeLocation(block.serverLocation(), TeleportScanners.NO_SCAN.get(), TeleportHelperFilters.CONFIG.get())
                    .orElseThrow(() -> context.createException("command.jump.notsafe"));
        }

        // Get a safe location
        if (!Util.isLocationInWorldBorder(targetLocation)) {
            return context.errorResult("command.jump.outsideborder");
        }

        player.setLocation(targetLocation);
        context.sendMessage("command.jump.success");
        return context.successResult();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.maxJump = serviceCollection.configProvider().getModuleConfig(JumpConfig.class).getMaxJump();
    }
}
