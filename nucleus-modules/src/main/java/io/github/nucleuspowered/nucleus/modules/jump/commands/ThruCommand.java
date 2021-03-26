/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jump.commands;

import io.github.nucleuspowered.nucleus.core.Util;
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
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.util.blockray.RayTraceResult;
import org.spongepowered.api.world.LocatableBlock;

import java.util.Optional;

@Command(
        aliases = {"thru", "through"},
        basePermission = JumpPermissions.BASE_THRU,
        commandDescriptionKey = "thru",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = JumpPermissions.EXEMPT_COOLDOWN_THRU),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = JumpPermissions.EXEMPT_WARMUP_THRU),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = JumpPermissions.EXEMPT_COST_THRU)
        }
)
public class ThruCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private int maxThru = 20;

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer player = context.requirePlayer();

        final ThruState state = new ThruState();

        final Optional<RayTraceResult<LocatableBlock>> blockRayTraceResult = RayTrace.block()
                .sourceEyePosition(player)
                .direction(player.direction())
                .limit(this.maxThru)
                .select(state::select)
                .continueWhileBlock(state::continueWhile)
                .execute();

        if (!blockRayTraceResult.isPresent()) {
            if (state.hitWall) {
                // We didn't find anywhere to jump to.
                return context.errorResult("command.thru.nospot");
            } else {
                return context.errorResult("command.thru.nowall");
            }
        }

        final LocatableBlock block = blockRayTraceResult.get().selectedObject();

        // Get a safe location
        if (!Util.isLocationInWorldBorder(block.serverLocation())) {
            return context.errorResult("command.jump.outsideborder");
        }

        player.setLocation(block.serverLocation());
        context.sendMessage("command.thru.success");
        return context.successResult();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.maxThru = serviceCollection.configProvider().getModuleConfig(JumpConfig.class).getMaxThru();
    }

    static final class ThruState {

        boolean hitWall = false;

        boolean continueWhile(final LocatableBlock block) {
            if (RayTrace.nonAir().test(block)) {
                this.hitWall = true;
            }
            return true;
        }

        boolean select(final LocatableBlock block) {
            if (this.hitWall) {
                return RayTrace.onlyAir().test(block) && RayTrace.onlyAir().test(block.serverLocation().add(0, 1, 0).asLocatableBlock());
            }
            return false;
        }

    }

}
