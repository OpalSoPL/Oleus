/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.border;

import io.github.nucleuspowered.nucleus.modules.world.WorldKeys;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.modules.world.services.WorldHelper;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.world.storage.WorldProperties;

@Command(
        aliases = { "cancelgen" },
        basePermission = WorldPermissions.BASE_BORDER_GEN,
        commandDescriptionKey = "world.border.cancelgen",
        parentCommand = BorderCommand.class
)
public class CancelChunkGenCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY.get(serviceCollection)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties wp = context.getWorldPropertiesOrFromSelf(NucleusParameters.Keys.WORLD)
                .orElseThrow(() -> context.createException("command.world.player"));
        context.getServiceCollection()
                .storageManager()
                .getWorldService()
                .getOrNew(wp.getUniqueId())
                .thenAccept(x -> x.set(WorldKeys.WORLD_PREGEN_START, false));
        final WorldHelper worldHelper = context.getServiceCollection().getServiceUnchecked(WorldHelper.class);
        if (worldHelper.cancelPregenRunningForWorld(wp.getUniqueId())) {
            context.sendMessage("command.world.cancelgen.cancelled", wp.getWorldName());
            return context.successResult();
        }

        return context.errorResult("command.world.cancelgen.notask", wp.getWorldName());
    }
}
