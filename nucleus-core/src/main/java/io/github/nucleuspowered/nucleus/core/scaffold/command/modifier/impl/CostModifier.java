/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.impl;

import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.config.CommandModifiersConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IEconomyServiceProvider;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Optional;

public class CostModifier implements ICommandModifier {

    private static final String COST = "cost";

    @Override public void getDefaultNode(final ConfigurationNode node, final IMessageProviderService messageProviderService) {
        final ConfigurationNode n = node.node(COST);
        if (n instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode) n).comment(messageProviderService.getMessageString("config.cost"));
        }
        try {
            n.set(0.0);
        } catch (final SerializationException e) {
            e.printStackTrace();
        }
    }

    @Override public void setDataFromNode(final CommandModifiersConfig config, final ConfigurationNode node) {
        config.setCost(node.node(COST).getInt(0));
    }

    @Override public void setValueFromOther(final CommandModifiersConfig from, final CommandModifiersConfig to) {
        to.setCost(from.getCost());
    }

    @Override public boolean canExecuteModifier(final INucleusServiceCollection serviceCollection, final CommandContext source) {
        return serviceCollection.economyServiceProvider().serviceExists() && source.cause().root() instanceof ServerPlayer;
    }

    @Override public Optional<Component> testRequirement(final ICommandContext source, final CommandControl control,
            final INucleusServiceCollection serviceCollection, final CommandModifier modifier) throws CommandException {
        if (source.getCost() > 0) {
            final double cost = source.getCost();
            final IEconomyServiceProvider ies = serviceCollection.economyServiceProvider();
            if (!ies.withdrawFromPlayer(source.getIfPlayer().uniqueId(), cost, false)) {
                return Optional.of(serviceCollection.messageProvider().getMessageFor(
                        source.cause().audience(), "cost.nofunds", ies.getCurrencySymbol(source.getCost())));
            }

            // Add a fail action
            source.addFailAction(s -> {
                serviceCollection.economyServiceProvider();
                try {
                    ies.depositInPlayer(s.getIfPlayer().uniqueId(), source.getCost(), false);
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
            ies.depositInPlayer(source.getIfPlayer().uniqueId(), source.getCost(), false);
        }
    }
}
