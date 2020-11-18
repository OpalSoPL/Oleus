/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.commands;

import io.github.nucleuspowered.nucleus.configurate.config.CommonPermissionLevelConfig;
import io.github.nucleuspowered.nucleus.modules.mute.MutePermissions;
import io.github.nucleuspowered.nucleus.modules.mute.config.MuteConfig;
import io.github.nucleuspowered.nucleus.modules.mute.services.MuteHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.CauseStackManager;

@Command(
        aliases = { "unmute" },
        basePermission = MutePermissions.BASE_UNMUTE,
        commandDescriptionKey = "unmute",
        associatedPermissionLevelKeys = MutePermissions.MUTE_LEVEL_KEY
)
public class UnmuteCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private CommonPermissionLevelConfig levelConfig = new CommonPermissionLevelConfig();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONE_USER.get(serviceCollection)
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User user = context.requireOne(NucleusParameters.Keys.USER, User.class);
        final MuteHandler handler = context.getServiceCollection().getServiceUnchecked(MuteHandler.class);
        if (this.levelConfig.isUseLevels() &&
                !context.isPermissionLevelOkay(user,
                        MutePermissions.MUTE_LEVEL_KEY,
                        MutePermissions.BASE_MUTE,
                        this.levelConfig.isCanAffectSameLevel())) {
            // Failure.
            return context.errorResult("command.modifiers.level.insufficient", user.getName());
        }

        if (!handler.isMuted(user)) {
            return context.errorResult("command.unmute.notmuted", user.getName());
        }
        // Unmute.
        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(context.getCommandSourceRoot());
            handler.unmutePlayer(user, frame.getCurrentCause(), false);
            context.sendMessage("command.unmute.success", user.getName(), context.getName());
            return context.successResult();
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        final MuteConfig config = serviceCollection.configProvider().getModuleConfig(MuteConfig.class);
        this.levelConfig = config.getLevelConfig();
    }
}
