/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.commands;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;
import io.github.nucleuspowered.nucleus.modules.playerinfo.PlayerInfoPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;

@Command(
        aliases = {"getpos", "coords", "position", "whereami", "getlocation", "getloc"},
        basePermission = PlayerInfoPermissions.BASE_GETPOS,
        commandDescriptionKey = "getpos",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = PlayerInfoPermissions.EXEMPT_COOLDOWN_GETPOS),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = PlayerInfoPermissions.EXEMPT_WARMUP_GETPOS),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = PlayerInfoPermissions.EXEMPT_COST_GETPOS)
        },
        associatedPermissions = PlayerInfoPermissions.GETPOS_OTHERS
)
public class GetPosCommand implements ICommandExecutor {

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.commandElementSupplier().createOnlyOtherUserPermissionElement(PlayerInfoPermissions.GETPOS_OTHERS)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User user = context.getUserFromArgs();
        final ServerLocation location;
        if (user.isOnline()) {
            location = user.getPlayer().get().getServerLocation();
        } else {
            final ServerWorld w = Sponge.server().worldManager().getWorld(user.getWorldKey())
                            .orElseThrow(() -> context.createException("command.getpos.location.nolocation", user.name()));
            location = ServerLocation.of(w, user.getPosition());
        }

        final boolean isSelf = context.is(user);
        final Vector3i blockPos = location.getBlockPosition();
        if (isSelf) {
            context.sendMessage(
                            "command.getpos.location.self",
                            location.getWorldKey().getFormatted(),
                            String.valueOf(blockPos.getX()),
                            String.valueOf(blockPos.getY()),
                            String.valueOf(blockPos.getZ())
            );
        } else {
            context.getMessage(
                            "command.getpos.location.other",
                            context.getDisplayName(user.getUniqueId()),
                            location.getWorldKey().getFormatted(),
                            String.valueOf(blockPos.getX()),
                            String.valueOf(blockPos.getY()),
                            String.valueOf(blockPos.getZ())
                    ).clickEvent(ClickEvent.runCommand(String.join(" ",
                        "/nucleus:tppos",
                            location.getWorldKey().asString(),
                            String.valueOf(blockPos.getX()),
                            String.valueOf(blockPos.getY()),
                            String.valueOf(blockPos.getZ()))))
                        .hoverEvent(HoverEvent.showText(
                                context.getMessage("command.getpos.hover")));
        }

        return context.successResult();
    }
}
