/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.commands;

import io.github.nucleuspowered.nucleus.api.module.home.data.Home;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportResult;
import io.github.nucleuspowered.nucleus.modules.home.HomePermissions;
import io.github.nucleuspowered.nucleus.modules.home.config.HomeConfig;
import io.github.nucleuspowered.nucleus.modules.home.parameters.HomeOtherArgument;
import io.github.nucleuspowered.nucleus.modules.home.services.HomeService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
@Command(
        aliases = {"other", "#homeother"},
        basePermission = HomePermissions.BASE_HOME_OTHER,
        commandDescriptionKey = "home.other",
        parentCommand = HomeCommand.class,
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = HomePermissions.EXEMPT_COOLDOWN_HOME_OTHER),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = HomePermissions.EXEMPT_WARMUP_HOME_OTHER),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = HomePermissions.EXEMPT_COST_HOME_OTHER)
        }
)
public class HomeOtherCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final String home = "home";
    private boolean isSafeTeleport = true;

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.isSafeTeleport = serviceCollection.configProvider().getModuleConfig(HomeConfig.class).isSafeTeleport();
    }

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                GenericArguments.onlyOne(
                        new HomeOtherArgument(
                                Text.of(this.home),
                                serviceCollection.getServiceUnchecked(HomeService.class),
                                serviceCollection))
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Get the home.
        final Home wl = context.requireOne(this.home, Home.class);
        final HomeService service = context.getServiceCollection().getServiceUnchecked(HomeService.class);

        final Player player = context.getIfPlayer();
        final TeleportResult result = service.warpToHome(
                            player,
                            wl,
                            this.isSafeTeleport
                    );

        // Warp to it safely.
        if (result.isSuccessful()) {
            context.sendMessage("command.homeother.success", wl.getUser().getName(), wl.getName());
            return context.successResult();
        } else {
            return context.errorResult("command.homeother.fail", wl.getUser().getName(), wl.getName());
        }
    }
}
