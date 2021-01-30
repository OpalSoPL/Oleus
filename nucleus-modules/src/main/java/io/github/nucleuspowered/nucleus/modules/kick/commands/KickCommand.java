/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kick.commands;

import io.github.nucleuspowered.nucleus.core.configurate.config.CommonPermissionLevelConfig;
import io.github.nucleuspowered.nucleus.modules.kick.KickPermissions;
import io.github.nucleuspowered.nucleus.modules.kick.config.KickConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@EssentialsEquivalent("kick")
@Command(
        aliases = "kick",
        basePermission = KickPermissions.BASE_KICK,
        commandDescriptionKey = "kick",
        associatedPermissionLevelKeys = KickPermissions.LEVEL_KEY,
        associatedPermissions = {
                KickPermissions.KICK_EXEMPT_TARGET,
                KickPermissions.KICK_NOTIFY
        }
)
public class KickCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private CommonPermissionLevelConfig levelConfig = new CommonPermissionLevelConfig();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONE_PLAYER,
                NucleusParameters.OPTIONAL_REASON_COMPONENT
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer pl = context.requireOne(NucleusParameters.ONE_PLAYER);
        final Component r = context.getOne(NucleusParameters.OPTIONAL_REASON_COMPONENT)
                .orElseGet(() -> context.getMessage("command.kick.defaultreason"));

        if (context.isConsoleAndBypass() && context.testPermissionFor(pl, KickPermissions.KICK_EXEMPT_TARGET)) {
            return context.errorResult("command.kick.exempt", pl.getName());
        }

        if (this.levelConfig.isUseLevels() &&
                !context.isPermissionLevelOkay(pl,
                        KickPermissions.LEVEL_KEY,
                        KickPermissions.BASE_KICK,
                        this.levelConfig.isCanAffectSameLevel())) {
            // Failure.
            return context.errorResult("command.modifiers.level.insufficient", pl.getName());
        }

        if (pl.kick(r)) {

            final Audience audience = context.getServiceCollection().permissionService().permissionMessageChannel(KickPermissions.KICK_NOTIFY);
            context.sendMessageTo(audience, "command.kick.message", pl.getName(), context.getName());
            context.sendMessageTo(audience, "command.reason", r);
            return context.successResult();
        }
        return context.errorResult("command.kick.failevent");
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.levelConfig = serviceCollection.configProvider().getModuleConfig(KickConfig.class).getLevelConfig();
    }
}
