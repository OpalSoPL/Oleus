/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;
import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.playerinfo.PlayerInfoPermissions;
import io.github.nucleuspowered.nucleus.modules.playerinfo.config.PlayerInfoConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPlayerOnlineService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.util.Tuple;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EssentialsEquivalent("near")
@Command(
        aliases = {"near"},
        basePermission = PlayerInfoPermissions.BASE_NEAR,
        commandDescriptionKey = "near",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = PlayerInfoPermissions.EXEMPT_COOLDOWN_NEAR),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = PlayerInfoPermissions.EXEMPT_WARMUP_NEAR),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = PlayerInfoPermissions.EXEMPT_COST_NEAR)
        }
)
public class NearCommand implements ICommandExecutor, IReloadableService.Reloadable {
        // SimpleReloadable {

    private static final NumberFormat formatter =  NumberFormat.getInstance();
    private final Parameter.Value<Integer> radiusParameter = Parameter.builder(Integer.class)
            .parser(VariableValueParameters.integerRange().setMin(1).build())
            .setKey("radius")
            .optional()
            .build();
    private int maxRadius;

    static {
        formatter.setMaximumFractionDigits(2);
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier()
                    .createOnlyOtherUserPermissionElement(PlayerInfoPermissions.OTHERS_NEAR),
                this.radiusParameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User user = context.getUserFromArgs();
        final ServerLocation location;
        final Vector3d position;
        if (user.isOnline()) {
            location = user.getPlayer().get().getServerLocation();
            position = location.getPosition();
        } else {
            final ServerWorld world = Sponge.server().worldManager().getWorld(user.getWorldKey())
                    .orElseThrow((() -> context.createException("command.near.location.nolocation", user.name())));
            position = user.getPosition();
            location = ServerLocation.of(world, position);
        }

        int radius = this.maxRadius;
        final Optional<Integer> radiusOpt = context.getOne(this.radiusParameter);
        if (radiusOpt.isPresent()) {
            final int inputRadius = radiusOpt.get();
            // Check if executor has max radius override permission
            if (inputRadius > this.maxRadius && context.testPermission(PlayerInfoPermissions.EXEMPT_MAXRADIUS_NEAR)) {
                radius = inputRadius;
            } else {
                context.sendMessage("command.near.radiustoobig", this.maxRadius);
            }
        }

        final Optional<ServerPlayer> src = context.getAsPlayer();
        final IPlayerOnlineService playerOnlineService = context.getServiceCollection().playerOnlineService();
        final List<Component> messagesToSend =
                location.getWorld()
                        .getNearbyEntities(location.getPosition(), radius)
                        .stream()
                        .filter(ServerPlayer.class::isInstance)
                        .map(ServerPlayer.class::cast)
                        .filter(e -> e.getUniqueId() != user.getUniqueId() && src.map(x -> playerOnlineService.isOnline(x, e.getUser())).orElse(true))
                        .map(x -> Tuple.of(x, position.distance(x.getPosition())))
                        .sorted(Comparator.comparingDouble(Tuple::getSecond))
                        .map(tuple -> this.createPlayerLine(context, tuple))
                        .collect(Collectors.toList());

        Util.getPaginationBuilder(context.getAudience())
                        .title(context.getMessage("command.near.playersnear", user.name()))
                        .contents(messagesToSend)
                        .sendTo(context.getAudience());

        return context.successResult();
    }

    private Component createPlayerLine(final ICommandContext context, final Tuple<ServerPlayer, Double> player) {
        final TextComponent.Builder line = Component.text();
        context.getMessage("command.near.playerdistancefrom", player.getFirst().getName());
        line.append(context.getMessage("command.near.playerdistancefrom", player.getFirst().getName(),
                formatter.format(Math.abs(player.getSecond()))))
                .clickEvent(ClickEvent.runCommand("/tp " + player.getFirst().getName()))
                .hoverEvent(HoverEvent.showText(context.getMessage("command.near.tpto", player.getFirst().getName())));
        return line.build();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        final PlayerInfoConfig configAdapter = serviceCollection
                .configProvider()
                .getModuleConfig(PlayerInfoConfig.class);
        this.maxRadius = configAdapter.getNear().getMaxRadius();
    }

}
