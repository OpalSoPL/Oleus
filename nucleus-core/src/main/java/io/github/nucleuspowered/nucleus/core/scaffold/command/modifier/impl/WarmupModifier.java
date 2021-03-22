/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.impl;

import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.config.CommandModifiersConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IWarmupService;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.util.Optional;

public class WarmupModifier implements ICommandModifier {

    private static final String WARMUP = "warmup";

    @Override public void getDefaultNode(final ConfigurationNode node, final IMessageProviderService messageProviderService) {
        final ConfigurationNode n = node.node(WARMUP);
        if (n instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode) n).comment(messageProviderService.getMessageString("config.warmup"));
        }
        try {
            n.set(0);
        } catch (final SerializationException e) {
            e.printStackTrace();
        }
    }

    @Override public void setDataFromNode(final CommandModifiersConfig config, final ConfigurationNode node) {
        config.setWarmup(node.node(WARMUP).getInt(0));
    }

    @Override public void setValueFromOther(final CommandModifiersConfig from, final CommandModifiersConfig to) {
        to.setWarmup(from.getWarmup());
    }

    @Override public boolean canExecuteModifier(final INucleusServiceCollection serviceCollection, final CommandContext source) {
        return source.cause().root() instanceof ServerPlayer;
    }

    @Override public Optional<ICommandResult> preExecute(final ICommandContext source, final CommandControl control,
            final INucleusServiceCollection serviceCollection, final CommandModifier modifier) {
        if (source.getWarmup() > 0) {
            // If the player had an exemption earlier, this would not be in the list. Therefore, we have a warmup.
            // We also know we have a player.
            final Player player = source.getCommandSourceAsPlayerUnchecked();
            serviceCollection.warmupService().cancel(player);

            // Send a message.
            serviceCollection.warmupService().executeAfter(player, Duration.ofSeconds(source.getWarmup()), new IWarmupService.WarmupTask() {
                @Override public void run() {
                    serviceCollection.messageProvider().sendMessageTo(player, "warmup.end");
                    control.startExecute(source);
                }

                @Override public void onCancel() {
                    control.onFail(source, null); // the message is handled elsewhere
                }
            }, true);

            return Optional.of(ICommandResult.willContinueLater());
        }

        return Optional.empty();
    }

}
