/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.NucleusWorldPropertiesArgument;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.CommandFlags;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(
        aliases = {"unload"},
        basePermission = WorldPermissions.BASE_WORLD_UNLOAD,
        commandDescriptionKey = "world.unload",
        parentCommand = WorldCommand.class
)
public class UnloadWorldCommand implements ICommandExecutor {

    private final String transferWorldKey = "transferWorld";

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
            GenericArguments.flags()
                .permissionFlag(WorldPermissions.BASE_WORLD_DISABLE, "d", "-disable")
                .valueFlag(new NucleusWorldPropertiesArgument(Text.of(this.transferWorldKey), NucleusWorldPropertiesArgument.Type.ENABLED_ONLY, serviceCollection),
                        "t", "-transfer")
                .setUnknownShortFlagBehavior(CommandFlags.UnknownFlagBehavior.IGNORE)
                .buildWith(NucleusParameters.WORLD_PROPERTIES_ENABLED_ONLY.get(serviceCollection))
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties worldProperties = context.requireOne(NucleusParameters.Keys.WORLD, WorldProperties.class);
        final Optional<WorldProperties> transferWorld = context.getOne(this.transferWorldKey, WorldProperties.class);
        final boolean disable = context.hasAny("d");

        final Optional<World> worldOptional = Sponge.getServer().getWorld(worldProperties.getUniqueId());
        if (!worldOptional.isPresent()) {
            // Not loaded.
            if (disable) {
                disable(worldProperties, context, false);
            }

            return context.errorResult("command.world.unload.alreadyunloaded", worldProperties.getWorldName());
        }

        final World world = worldOptional.get();
        final List<Player> playerCollection = Sponge.getServer().getOnlinePlayers().stream().filter(x -> x.getWorld().equals(world)).collect(Collectors.toList());

        if ((transferWorld.isPresent() && transferWorld.get().isEnabled())) {
            playerCollection.forEach(x -> x.transferToWorld(transferWorld.get().getUniqueId(), transferWorld.get().getSpawnPosition().toDouble()));
        }

        return unloadWorld(context, world, disable);
    }

    private static ICommandResult disable(final WorldProperties worldProperties,
            final ICommandContext context,
            final boolean messageOnError) {
        if (worldProperties.isEnabled()) {
            return DisableWorldCommand.disableWorld(context, worldProperties);
        } else if (messageOnError) {
            return context.errorResult("command.world.disable.alreadydisabled", worldProperties.getWorldName());
        }

        return context.successResult();
    }

    private static ICommandResult unloadWorld(final ICommandContext context, final World world, final boolean disable) {
        final WorldProperties worldProperties = world.getProperties();
        context.sendMessage("command.world.unload.start", worldProperties.getWorldName());
        final boolean unloaded = Sponge.getServer().unloadWorld(world);
        if (unloaded) {
            context.sendMessage("command.world.unload.success", worldProperties.getWorldName());
            if (disable) {
                return disable(worldProperties, context, true);
            }

            return context.successResult();
        } else {
            return context.errorResult("command.world.unload.failed", worldProperties.getWorldName());
        }
    }
}
