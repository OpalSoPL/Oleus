/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.api.module.kit.KitRedeemResult;
import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfig;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.Player;

import java.time.Duration;

/**
 * Gives a kit to a subject.
 */
@Command(
        aliases = { "give" },
        basePermission = KitPermissions.BASE_KIT_GIVE,
        commandDescriptionKey = "kit.give",
        parentCommand = KitCommand.class,
        associatedPermissions = KitPermissions.KIT_GIVE_OVERRIDE
)
public class KitGiveCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private boolean mustGetAll;
    private boolean isDrop;

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        final IPermissionService permissionService = serviceCollection.permissionService();
        return new Flag[] {
                Flag.builder().alias("i").alias("ignore").setRequirement(cause -> permissionService.hasPermission(cause,
                        KitPermissions.KIT_GIVE_OVERRIDE)).build()
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONE_PLAYER,
                serviceCollection.getServiceUnchecked(KitService.class).kitParameterWithoutPermission()
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final KitService service = context.getServiceCollection().getServiceUnchecked(KitService.class);
        final Kit kit = context.requireOne(KitService.KIT_KEY);
        final Player player = context.requireOne(NucleusParameters.ONE_PLAYER);
        final boolean skip = context.hasFlag("i");
        if (context.is(player)) {
            return context.errorResult("command.kit.give.self");
        }

        final Component playerName = context.getDisplayName(player.uniqueId());
        final Component kitName = Component.text(kit.getName());
        final KitRedeemResult redeemResult = service.redeemKit(kit, player.uniqueId(), !skip, this.mustGetAll);
        if (redeemResult.isSuccess()) {
            if (!redeemResult.rejectedItems().isEmpty()) {
                // If we drop them, tell the user
                if (this.isDrop) {
                    context.sendMessage("command.kit.give.itemsdropped", playerName);
                    redeemResult.rejectedItems().forEach(x -> Util.dropItemOnFloorAtLocation(x, player.serverLocation()));
                } else {
                    context.sendMessage("command.kit.give.fullinventory", playerName);
                }
            }

            context.sendMessage("command.kit.give.spawned", playerName, kitName);
            if (kit.isDisplayMessageOnRedeem()) {
                context.sendMessage("command.kit.spawned", kit.getName());
            }

            return context.successResult();
        } else {
            switch (redeemResult.getStatus()) {
                case ALREADY_REDEEMED_ONE_TIME:
                    return context.errorResult("command.kit.give.onetime.alreadyredeemed", kitName, playerName);
                case COOLDOWN_NOT_EXPIRED:
                    return context.errorResult("command.kit.give.cooldown",
                            playerName,
                            context.getTimeString(redeemResult.getCooldownDuration().orElse(Duration.ZERO)),
                            kitName);
                case PRE_EVENT_CANCELLED:
                    return redeemResult
                            .getMessage()
                            .map(context::errorResultLiteral)
                            .orElseGet(() -> context.errorResult("command.kit.cancelledpre", kit.getName()));
                case NO_SPACE:
                    return context.errorResult("command.kit.give.fullinventorynosave", playerName);
                case UNKNOWN:
                default:
                    return context.errorResult("command.kit.give.fail", playerName, kitName);
            }
        }

    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        final KitConfig kca = serviceCollection.configProvider().getModuleConfig(KitConfig.class);
        this.isDrop = kca.isDropKitIfFull();
        this.mustGetAll = kca.isMustGetAll();
    }

}
