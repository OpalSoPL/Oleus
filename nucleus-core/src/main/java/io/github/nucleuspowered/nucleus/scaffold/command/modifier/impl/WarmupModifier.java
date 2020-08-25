/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl;

import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.core.config.WarmupConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.config.CommandModifiersConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.services.interfaces.IWarmupService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.time.Duration;
import java.util.Optional;

public class WarmupModifier implements ICommandModifier, IReloadableService.Reloadable {

    private static final String WARMUP = "warmup";

    private WarmupConfig warmupConfig = new WarmupConfig();

    @Override public void getDefaultNode(final ConfigurationNode node, final IMessageProviderService messageProviderService) {
        final ConfigurationNode n = node.getNode(WARMUP);
        if (n instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode) n).setComment(messageProviderService.getMessageString("config.warmup"));
        }
        n.setValue(0);
    }

    @Override public void setDataFromNode(final CommandModifiersConfig config, final ConfigurationNode node) {
        config.setWarmup(node.getNode(WARMUP).getInt(0));
    }

    @Override public void setValueFromOther(final CommandModifiersConfig from, final CommandModifiersConfig to) {
        to.setWarmup(from.getWarmup());
    }

    @Override public boolean canExecuteModifier(final INucleusServiceCollection serviceCollection, final CommandContext source) throws
            CommandException {
        return source.getCause().root() instanceof ServerPlayer;
    }

    @Override public Optional<ICommandResult> preExecute(final ICommandContext source, final CommandControl control,
            final INucleusServiceCollection serviceCollection, final CommandModifier modifier) {
        if (source.getWarmup() > 0) {
            // If the player had an exemption earlier, this would not be in the list. Therefore, we have a warmup.
            // We also know we have a player.
            final Player player = source.getCommandSourceAsPlayerUnchecked();
            serviceCollection.warmupService().cancel(player);

            // Send a message.
            serviceCollection.messageProvider().sendMessageTo(player, "warmup.start",
                    serviceCollection.messageProvider().getTimeString(player.getLocale(), source.getWarmup()));
            if (this.warmupConfig.isOnCommand() && this.warmupConfig.isOnMove()) {
                serviceCollection.messageProvider().sendMessageTo(player, "warmup.both");
            } else if (this.warmupConfig.isOnCommand()) {
                serviceCollection.messageProvider().sendMessageTo(player, "warmup.onCommand");
            } else if (this.warmupConfig.isOnMove()) {
                serviceCollection.messageProvider().sendMessageTo(player, "warmup.onMove");
            }
            serviceCollection.warmupService().executeAfter(player, Duration.ofSeconds(source.getWarmup()), new IWarmupService.WarmupTask() {
                @Override public void run() {
                    serviceCollection.messageProvider().sendMessageTo(player, "warmup.end");
                    control.startExecute(source);
                }

                @Override public void onCancel() {
                    control.onFail(source, serviceCollection.messageProvider().getMessage("warmup.cancel"));
                }
            });

            return Optional.of(ICommandResult.willContinueLater());
        }

        return Optional.empty();
    }


    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.warmupConfig = serviceCollection.moduleDataProvider().getModuleConfig(CoreConfig.class).getWarmupConfig();
    }
}
