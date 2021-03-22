/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jump.commands;

import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanners;
import io.github.nucleuspowered.nucleus.modules.jump.JumpPermissions;
import io.github.nucleuspowered.nucleus.modules.jump.config.JumpConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.teleport.TeleportHelperFilters;

@Command(
        aliases = {"unstuck"},
        basePermission = JumpPermissions.BASE_UNSTUCK,
        commandDescriptionKey = "unstuck",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = JumpPermissions.EXEMPT_COOLDOWN_UNSTUCK),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = JumpPermissions.EXEMPT_WARMUP_UNSTUCK),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = JumpPermissions.EXEMPT_COST_UNSTUCK)
        }
)
public class UnstuckCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private int radius = 1;
    private int height = 1;

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Get the player location, find a safe location. Prevent players trying this to get out of sticky situations, height and width is 1.
        final ServerPlayer src = context.requirePlayer();
        final ServerLocation location = Sponge.server().getTeleportHelper().getSafeLocation(src.getServerLocation(), this.height, this.radius)
            .orElseThrow(() -> context.createException("command.unstuck.nolocation"));
        if (location.getBlockPosition().equals(src.getLocation().getBlockPosition())) {
            return context.errorResult("command.unstuck.notneeded");
        }

        if (context.getServiceCollection().teleportService().teleportPlayer(
                src,
                location,
                src.getRotation(),
                false,
                TeleportScanners.NO_SCAN.get(),
                TeleportHelperFilters.DEFAULT.get()).isSuccessful()) {
            context.sendMessage("command.unstuck.success");
            return context.successResult();
        }

        return context.errorResult("command.unstuck.cancelled");
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        final JumpConfig c = serviceCollection.configProvider().getModuleConfig(JumpConfig.class);
        this.radius = c.getMaxUnstuckRadius();
        this.height = c.getMaxUnstuckHeight();
    }
}
