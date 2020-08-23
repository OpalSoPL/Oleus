/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.commands;

import io.github.nucleuspowered.nucleus.configurate.config.CommonPermissionLevelConfig;
import io.github.nucleuspowered.nucleus.modules.jail.JailPermissions;
import io.github.nucleuspowered.nucleus.modules.jail.config.JailConfig;
import io.github.nucleuspowered.nucleus.modules.jail.services.JailHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.CauseStackManager;
import com.google.inject.Inject;

@Command(
        aliases = {"unjail"},
        basePermission = JailPermissions.JAIL_UNJAIL,
        commandDescriptionKey = "unjail",
        associatedPermissionLevelKeys = JailPermissions.JAIL_LEVEL_KEY
)
@EssentialsEquivalent(value = "unjail", isExact = false, notes = "Not a toggle.")
public class UnjailCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final JailHandler handler;
    private CommonPermissionLevelConfig levelConfig = new CommonPermissionLevelConfig();

    @Inject
    public UnjailCommand(final INucleusServiceCollection serviceCollection) {
        this.handler = serviceCollection.getServiceUnchecked(JailHandler.class);
    }

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.ONE_USER.get(serviceCollection)
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User user = context.requireOne(NucleusParameters.Keys.USER, User.class);
        if (this.levelConfig.isUseLevels() &&
                !context.isPermissionLevelOkay(user,
                        JailPermissions.JAIL_LEVEL_KEY,
                        JailPermissions.BASE_JAIL,
                        this.levelConfig.isCanAffectSameLevel())) {
            // Failure.
            return context.errorResult("command.modifiers.level.insufficient", user.getName());
        }

        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(context.getCommandSourceRoot());
            if (this.handler.unjailPlayer(user)) {
                context.sendMessage("command.jail.unjail.success", user.getName());
                return context.successResult();
            } else {
                return context.errorResult("command.jail.unjail.fail", user.getName());
            }
        }
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.levelConfig = serviceCollection.moduleDataProvider().getModuleConfig(JailConfig.class).getCommonPermissionLevelConfig();
    }
}
