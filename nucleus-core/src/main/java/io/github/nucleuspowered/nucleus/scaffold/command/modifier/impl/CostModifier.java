/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.config.CommandModifiersConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IEconomyServiceProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.TextComponent;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Optional;

public class CostModifier implements ICommandModifier {

    private static final String COST = "cost";

    @Override public void getDefaultNode(final ConfigurationNode node, final IMessageProviderService messageProviderService) {
        final ConfigurationNode n = node.getNode(COST);
        if (n instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode) n).setComment(messageProviderService.getMessageString("config.cost"));
        }
        n.setValue(0.0);
    }

    @Override public void setDataFromNode(final CommandModifiersConfig config, final ConfigurationNode node) {
        config.setCost(node.getNode(COST).getInt(0));
    }

    @Override public void setValueFromOther(final CommandModifiersConfig from, final CommandModifiersConfig to) {
        to.setCost(from.getCost());
    }

    @Override public boolean canExecuteModifier(final INucleusServiceCollection serviceCollection, final CommandContext source) throws
            CommandException {
        return serviceCollection.economyServiceProvider().serviceExists() && source.getCause().root() instanceof ServerPlayer;
    }

    @Override public Optional<TextComponent> testRequirement(final ICommandContext source, final CommandControl control,
            final INucleusServiceCollection serviceCollection, final CommandModifier modifier) throws CommandException {
        if (source.getCost() > 0) {
            final double cost = source.getCost();
            final IEconomyServiceProvider ies = serviceCollection.economyServiceProvider();
            if (!ies.withdrawFromPlayer(source.getIfPlayer().getUniqueId(), cost, false)) {
                return Optional.of(serviceCollection.messageProvider().getMessageFor(
                        source.getCause().getAudience(), "cost.nofunds", ies.getCurrencySymbol(source.getCost())));
            }

            // Add a fail action
            source.addFailAction(s -> {
                serviceCollection.economyServiceProvider();
                try {
                    ies.depositInPlayer(s.getIfPlayer().getUniqueId(), source.getCost(), false);
                } catch (final CommandException e) {
                    serviceCollection.logger().error("Could not return {} to {}.", cost, source.getName());
                }
            });
        }

        return Optional.empty();
    }

    @Override
    public void onFailure(final ICommandContext source, final CommandControl control, final INucleusServiceCollection serviceCollection,
            final CommandModifier modifier) throws CommandException {
        if (source.getCost() > 0) {
            final IEconomyServiceProvider ies = serviceCollection.economyServiceProvider();
            ies.depositInPlayer(source.getIfPlayer().getUniqueId(), source.getCost(), false);
        }
    }
}
