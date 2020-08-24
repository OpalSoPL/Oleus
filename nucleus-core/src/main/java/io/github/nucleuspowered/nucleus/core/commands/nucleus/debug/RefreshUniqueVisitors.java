/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands.nucleus.debug;

import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.commands.nucleus.DebugCommand;
import io.github.nucleuspowered.nucleus.core.services.UniqueUserService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Command(
        aliases = "refreshuniquevisitors",
        basePermission = CorePermissions.BASE_DEBUG_REFRESHUNIQUEVISITORS,
        commandDescriptionKey = "nucleus.debug.refreshuniquevisitors",
        parentCommand = DebugCommand.class
)
public class RefreshUniqueVisitors implements ICommandExecutor {

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final UniqueUserService uus = context.getServiceCollection().getServiceUnchecked(UniqueUserService.class);
        context.sendMessage("command.nucleus.debug.refreshuniquevisitors.started", uus.getUniqueUserCount());

        final Optional<UUID> optionalUUID = context.getUniqueId();
        final Supplier<CommandSource> scs;
        if (optionalUUID.isPresent()) {
            final UUID uuid = optionalUUID.get();
            scs = () -> Sponge.getServer().getPlayer(uuid).map(x -> (CommandSource) x).orElseGet(Sponge.getServer()::getConsole);
        } else {
            scs = Sponge.getServer()::getConsole;
        }

        uus.resetUniqueUserCount(l -> context.sendMessageTo(scs.get(), "command.nucleus.debug.refreshuniquevisitors.done", l));
        return context.successResult();
    }
}
