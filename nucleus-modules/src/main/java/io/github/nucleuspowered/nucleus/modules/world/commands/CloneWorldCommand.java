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
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.UUID;
import java.util.function.Supplier;

@Command(
        aliases = {"clone", "copy"},
        basePermission = WorldPermissions.BASE_WORLD_CLONE,
        commandDescriptionKey = "world.clone",
        parentCommand = WorldCommand.class
)
public class CloneWorldCommand implements ICommandExecutor {

    private final String newKey = "new name";

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.WORLD_PROPERTIES_ALL.get(serviceCollection),
                GenericArguments.string(Text.of(this.newKey))
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties worldToCopy = context.requireOne(NucleusParameters.Keys.WORLD, WorldProperties.class);
        final String oldName = worldToCopy.getWorldName();
        final String newName = context.requireOne(this.newKey, String.class);
        if (Sponge.getServer().getWorldProperties(newName).isPresent()) {
            return context.errorResult("command.world.clone.alreadyexists", newName);
        }

        context.sendMessage("command.world.clone.starting", oldName, newName);
        if (!context.is(ConsoleSource.class)) {
            context.sendMessageTo(Sponge.getServer().getConsole(), "command.world.clone.starting", oldName, newName);
        }

        // Well, you never know, the player might die or disconnect - we have to be vigilant.
        final Supplier<MessageReceiver> mr;
        if (context.is(Player.class)) {
            final UUID uuid = context.getIfPlayer().getUniqueId();
            mr = () -> Sponge.getServer().getPlayer(uuid).map(x -> (MessageReceiver) x).orElseGet(() -> new MessageReceiver() {
                @Override public void sendMessage(final TextComponent message) {

                }

                @Override public MessageChannel getMessageChannel() {
                    return MessageChannel.TO_NONE;
                }

                @Override public void setMessageChannel(final MessageChannel channel) {

                }
            });
        } else {
            mr = context::getCommandSourceRoot;
        }

        Sponge.getServer().copyWorld(worldToCopy, newName).handle((result, ex) -> {

            final MessageReceiver m = mr.get();
            if (ex == null && result.isPresent()) {
                context.sendMessage("command.world.clone.success", oldName, newName);
                if (!(m instanceof ConsoleSource)) {
                    context.sendMessageTo(Sponge.getServer().getConsole(), "command.world.clone.success", oldName, newName);
                }
            } else {
                context.sendMessage("command.world.clone.failed", oldName, newName);
                if (!(m instanceof ConsoleSource)) {
                    context.sendMessageTo(Sponge.getServer().getConsole(), "command.world.clone.failed", oldName, newName);
                }
            }

            return result;
        });

        return context.successResult();
    }
}
