/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.afk.commands;

import io.github.nucleuspowered.nucleus.modules.afk.AFKPermissions;
import io.github.nucleuspowered.nucleus.modules.afk.services.AFKHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Collection;
import java.util.Optional;

@Command(aliases = {"afkkick", "kickafk"}, basePermission = AFKPermissions.BASE_AFKKICK, commandDescriptionKey = "afkkick")
public class AFKKickCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_REASON
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Optional<Component> reason = context
                .getOne(NucleusParameters.REASON)
                .map(LegacyComponentSerializer.legacyAmpersand()::deserialize);

        final Collection<ServerPlayer> playersToKick = context.getServiceCollection().getServiceUnchecked(AFKHandler.class)
                .getAfk(x -> !context.testPermissionFor(x, AFKPermissions.AFK_EXEMPT_KICK));
        if (playersToKick.isEmpty()) {
            return context.errorResult("command.afkkick.nokick");
        }

        final IMessageProviderService messageProviderService = context.getServiceCollection().messageProvider();
        final int number = playersToKick.size();
        playersToKick.forEach(x -> x.kick(reason.orElseGet(() -> messageProviderService.getMessageFor(x.getLocale(), "afk.kickreason"))));

        context.sendMessage("command.afkkick.success", number);
        return context.successResult();
    }
}
