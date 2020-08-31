/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.commands;

import io.github.nucleuspowered.nucleus.api.module.home.NucleusHomeService;
import io.github.nucleuspowered.nucleus.api.module.home.data.Home;
import io.github.nucleuspowered.nucleus.api.module.home.exception.HomeException;
import io.github.nucleuspowered.nucleus.modules.home.HomePermissions;
import io.github.nucleuspowered.nucleus.modules.home.config.HomeConfig;
import io.github.nucleuspowered.nucleus.modules.home.services.HomeService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import java.util.Optional;

@EssentialsEquivalent({"sethome", "createhome"})
@Command(
        aliases = { "set", "#homeset", "#sethome" },
        basePermission = HomePermissions.BASE_HOME_SET,
        commandDescriptionKey = "home.set",
        parentCommand = HomeCommand.class,
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = HomePermissions.EXEMPT_COOLDOWN_HOME_SET),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = HomePermissions.EXEMPT_WARMUP_HOME_SET),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = HomePermissions.EXEMPT_COST_HOME_SET)
        },
        associatedPermissions = HomePermissions.HOMES_UNLIMITED,
        associatedOptions = HomePermissions.OPTION_HOME_COUNT
)
public class SetHomeCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final String homeKey = "home";

    private boolean preventOverhang = true;

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
            GenericArguments.flags().flag("o", "-overwrite").buildWith(
                GenericArguments.onlyOne(GenericArguments.optional(GenericArguments.string(Text.of(homeKey))))
            )
        };
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.preventOverhang = serviceCollection.configProvider()
                .getModuleConfig(HomeConfig.class)
                .isPreventHomeCountOverhang();
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Get the home key.
        final String home = context.getOne(this.homeKey, String.class).orElse(NucleusHomeService.DEFAULT_HOME_NAME).toLowerCase();

        if (!NucleusHomeService.HOME_NAME_PATTERN.matcher(home).matches()) {
            return context.errorResult("command.sethome.name");
        }

        final Player src = context.getIfPlayer();
        final HomeService homeService = context.getServiceCollection().getServiceUnchecked(HomeService.class);
        final Optional<Home> currentHome = homeService.getHome(src, home);
        final boolean overwrite = currentHome.isPresent() && context.hasAny("o");
        if (currentHome.isPresent() && !overwrite) {
            context.sendMessage("command.sethome.seterror", home);
            context.sendMessageText(
                    context.getMessage("command.sethome.tooverwrite", home).toBuilder()
                        .onClick(TextActions.runCommand("/sethome " + home + " -o")).build());
            return context.failResult();
        }

        try {
            if (overwrite) {
                final int max = homeService.getMaximumHomes(src) ;
                final int c = homeService.getHomeCount(src) ;
                if (this.preventOverhang && max < c) {
                    // If the player has too many homes, tell them
                    context.errorResult("command.sethome.overhang", max, c);
                }

                final Home current = currentHome.get();
                homeService.modifyHomeInternal(context.getCause(), current, src.getLocation(), src.getRotation());
                context.sendMessage("command.sethome.overwrite", home);
            } else {
                homeService.createHomeInternal(context.getCause(), src, home, src.getLocation(), src.getRotation());
            }
        } catch (final HomeException e) {
            e.printStackTrace();
            return context.errorResultLiteral(e.getText());
        }

        context.sendMessage("command.sethome.set", home);
        return context.successResult();
    }
}
