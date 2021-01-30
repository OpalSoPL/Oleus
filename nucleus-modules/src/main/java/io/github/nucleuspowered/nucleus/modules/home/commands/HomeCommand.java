/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.commands;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.module.home.NucleusHomeService;
import io.github.nucleuspowered.nucleus.api.module.home.data.Home;
import io.github.nucleuspowered.nucleus.api.module.home.exception.HomeException;
import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportResult;
import io.github.nucleuspowered.nucleus.modules.home.HomePermissions;
import io.github.nucleuspowered.nucleus.modules.home.config.HomeConfig;
import io.github.nucleuspowered.nucleus.modules.home.events.UseHomeEvent;
import io.github.nucleuspowered.nucleus.modules.home.parameters.HomeParameter;
import io.github.nucleuspowered.nucleus.modules.home.services.HomeService;
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
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Optional;
import java.util.UUID;

@EssentialsEquivalent(value = {"home", "homes"}, notes = "'/homes' will list homes, '/home' will teleport like Essentials did.")
@Command(
        aliases = {"home"},
        basePermission = HomePermissions.BASE_HOME,
        commandDescriptionKey = "home",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = HomePermissions.EXEMPT_COOLDOWN_HOME),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = HomePermissions.EXEMPT_WARMUP_HOME),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = HomePermissions.EXEMPT_COST_HOME)
        },
        associatedPermissions = {
                HomePermissions.HOME_EXEMPT_SAMEDIMENSION,
                HomePermissions.BASE_HOME_OTHER
        }
)
public class HomeCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private boolean isSafeTeleport = true;
    private boolean isPreventOverhang = true;
    private boolean isOnlySameDimension = false;

    private final Parameter.Value<Home> parameter;
    private final Parameter.Value<User> userParameter;

    @Inject
    public HomeCommand(final INucleusServiceCollection serviceCollection) {
        final IPermissionService permissionService = serviceCollection.permissionService();
        this.userParameter =
                Parameter.user()
                        .setRequirements(x -> permissionService.hasPermission(x, HomePermissions.BASE_HOME_OTHER))
                        .setKey(HomeParameter.OTHER_PLAYER_KEY)
                        .build();
        this.parameter = Parameter.builder(Home.class)
                .parser(new HomeParameter(serviceCollection.getServiceUnchecked(HomeService.class), serviceCollection.messageProvider()))
                .optional()
                .setKey("home")
                .build();
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                Parameter.firstOf(
                       this.parameter,
                       Parameter.seq(
                               this.userParameter,
                               this.parameter
                       )
                )
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final HomeService homeService = context.getServiceCollection().getServiceUnchecked(HomeService.class);
        final UUID target;
        final User user;
        final ServerPlayer invokingPlayer = context.getIfPlayer();
        final boolean isOther;
        if (context.hasAny(this.userParameter)) {
            user = context.requireOne(this.userParameter);
            target = user.getUniqueId();
            isOther = true;
        } else {
            user = invokingPlayer.getUser();
            target = invokingPlayer.getUniqueId();
            isOther = false;
        }
        final int max = homeService.getMaximumHomes(target);
        final int current = homeService.getHomeCount(target);
        if (this.isPreventOverhang && max < current) {
            // If the player has too many homes, tell them
            return context.errorResult("command.home.overhang", max, current);
        }

        // Get the home.
        final Optional<Home> owl = context.getOne(this.parameter);
        final Home wl;
        if (owl.isPresent()) {
            wl = owl.get();
        } else {
            final Optional<Home> home = homeService.getHome(target, NucleusHomeService.DEFAULT_HOME_NAME);
            if (!home.isPresent()) {
                return context.errorResult("args.home.nohome", NucleusHomeService.DEFAULT_HOME_NAME);
            }
            wl = home.get();
        }

        Sponge.getServer().getWorldManager().getWorld(wl.getWorldProperties().get().getKey())
                .orElseThrow(() -> context.createException("command.home.invalid", wl.getName()));

        final ServerLocation targetLocation = wl.getLocation().orElseThrow(() -> context.createException("command.home.invalid", wl.getName()));

        if (this.isOnlySameDimension) {
            if (!targetLocation.getWorldKey().equals(user.getWorldKey())) {
                if (!context.testPermission(HomePermissions.HOME_EXEMPT_SAMEDIMENSION)) {
                    return context.errorResult("command.home.invalid", wl.getName());
                }
            }
        }

        try (final CauseStackManager.StackFrame frame = Sponge.getServer().getCauseStackManager().pushCauseFrame()) {
            final UseHomeEvent event = new UseHomeEvent(frame.getCurrentCause(), target, wl);

            if (Sponge.getEventManager().post(event)) {
                return event.getCancelMessage().map(context::errorResultLiteral)
                        .orElseGet(() -> context.errorResult("nucleus.eventcancelled"));
            }
        }


        final TeleportResult result;
        try {
            result = homeService.warpToHome(
                    invokingPlayer,
                    wl,
                    this.isSafeTeleport
            );
        } catch (final HomeException ex) {
            return context.errorResultLiteral(Component.text(ex.getMessage()));
        }

        if (isOther) {
            // Warp to it safely.
            if (result.isSuccessful()) {
                context.sendMessage("command.homeother.success", user.getName(), wl.getName());
                return context.successResult();
            } else {
                return context.errorResult("command.homeother.fail", user.getName(), wl.getName());
            }
        }

        // Warp to it safely.
        if (result.isSuccessful()) {
            if (!wl.getName().equalsIgnoreCase(NucleusHomeService.DEFAULT_HOME_NAME)) {
                context.sendMessage("command.home.success", wl.getName());
            } else {
                context.sendMessage("command.home.successdefault");
            }

            return context.successResult();
        } else {
            return context.errorResult("command.home.fail", wl.getName());
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        final HomeConfig hc = serviceCollection.configProvider().getModuleConfig(HomeConfig.class);
        this.isSafeTeleport = hc.isSafeTeleport();
        this.isPreventOverhang = hc.isPreventHomeCountOverhang();
        this.isOnlySameDimension = hc.isOnlySameDimension();
    }
}
