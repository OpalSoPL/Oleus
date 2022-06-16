/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.modules.teleport.TeleportPermissions;
import io.github.nucleuspowered.nucleus.modules.teleport.config.TeleportConfig;
import io.github.nucleuspowered.nucleus.modules.teleport.events.RequestEvent;
import io.github.nucleuspowered.nucleus.modules.teleport.services.PlayerTeleporterService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

@Command(
        aliases = {"tpa", "teleportask", "call", "tpask"},
        basePermission = TeleportPermissions.BASE_TPA,
        commandDescriptionKey = "tpa",
        modifiers =
        {
                @CommandModifier(
                        value = CommandModifiers.HAS_WARMUP,
                        exemptPermission = TeleportPermissions.EXEMPT_WARMUP_TPA,
                        onExecute = false
                ),
                @CommandModifier(
                        value = CommandModifiers.HAS_COOLDOWN,
                        exemptPermission = TeleportPermissions.EXEMPT_COOLDOWN_TPA,
                        onCompletion = false
                ),
                @CommandModifier(
                        value = CommandModifiers.HAS_COST,
                        exemptPermission = TeleportPermissions.EXEMPT_COST_TPA
                )
        },
        associatedPermissions = {
                TeleportPermissions.TPTOGGLE_EXEMPT,
                TeleportPermissions.TELEPORT_ASK_FORCE
        }
)
@EssentialsEquivalent({"tpa", "call", "tpask"})
public class TeleportAskCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private boolean isCooldownOnAsk = false;

    @Override public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.builder().alias("f").setRequirement(cause ->
                        serviceCollection.permissionService().hasPermission(cause, TeleportPermissions.TELEPORT_ASK_FORCE)).build()
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONE_PLAYER
        };
    }

    @Override
    public Optional<ICommandResult> preExecute(final ICommandContext context) throws CommandException {
        if (!context.getServiceCollection().getServiceUnchecked(PlayerTeleporterService.class)
                .canTeleportTo(
                        context.requirePlayer(),
                        context.requireOne(NucleusParameters.ONE_PLAYER).user()
                )) {
            return Optional.of(context.failResult());
        }
        return Optional.empty();
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer target = context.requireOne(NucleusParameters.ONE_PLAYER);


        if (context.is(target)) {
            return context.errorResult("command.teleport.self");
        }

        final RequestEvent.CauseToPlayer event = new RequestEvent.CauseToPlayer(Sponge.server().causeStackManager().currentCause(), target.uniqueId());
        if (Sponge.eventManager().post(event)) {
            if (event.getCancelMessage().isPresent()) {
                return context.errorResultLiteral(event.getCancelMessage().get());
            } else {
                return context.errorResult("command.tpa.eventfailed");
            }
        }

        Consumer<Player> cooldownSetter = player -> {};
        if (this.isCooldownOnAsk) {
            this.setCooldown(context);
        } else {
            cooldownSetter = player -> this.setCooldown(context);
        }

        context.getServiceCollection().getServiceUnchecked(PlayerTeleporterService.class).requestTeleport(
                context.getIfPlayer(),
                target,
                context.getCost(),
                context.getWarmup(),
                context.getIfPlayer(),
                target,
                !context.hasFlag("f"),
                false,
                false,
                cooldownSetter,
                "command.tpa.question"
        );

        NucleusAPI.getAFKService().ifPresent(x -> x.notifyIsAfk(context.audience(), target.uniqueId()));

        return context.successResult();
    }

    private void setCooldown(final ICommandContext context) {
        try {
            context.getServiceCollection()
                    .cooldownService()
                    .setCooldown(
                            context.getCommandKey(),
                            context.getIfPlayer(),
                            Duration.ofSeconds(context.getCooldown())
                    );
        } catch (final CommandException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.isCooldownOnAsk = serviceCollection.configProvider()
                .getModuleConfig(TeleportConfig.class)
                .isCooldownOnAsk();
    }
}
