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
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.time.Duration;
import java.util.Optional;

public class CooldownModifier implements ICommandModifier {

    private static final String COOLDOWN = "cooldown";

    @Override public void getDefaultNode(final ConfigurationNode node, final IMessageProviderService messageProviderService) {
        final ConfigurationNode n = node.getNode(COOLDOWN);
        if (n instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode) n).setComment(messageProviderService.getMessageString("config.cooldown"));
        }
        n.setValue(0);
    }

    @Override public void setDataFromNode(final CommandModifiersConfig config, final ConfigurationNode node) {
        config.setCooldown(node.getNode(COOLDOWN).getInt(0));
    }

    @Override public void setValueFromOther(final CommandModifiersConfig from, final CommandModifiersConfig to) {
        to.setCooldown(from.getCooldown());
    }

    @Override public boolean canExecuteModifier(final INucleusServiceCollection serviceCollection, final CommandContext source) {
        return source.getCause().root() instanceof ServerPlayer;
    }

    @Override public Optional<Component> testRequirement(final ICommandContext source,
            final CommandControl control,
            final INucleusServiceCollection serviceCollection, final CommandModifier modifier) throws CommandException {
        final ServerPlayer player = source.getIfPlayer();
        return serviceCollection.cooldownService().getCooldown(control.getModifierKey(), source.getIfPlayer())
                .map(duration -> serviceCollection.messageProvider().getMessageFor(player, "cooldown.message",
                        source.getTimeString(duration.getSeconds())));
    }

    @Override public void onCompletion(final ICommandContext source,
            final CommandControl control,
            final INucleusServiceCollection serviceCollection, final CommandModifier modifier) throws CommandException {
        serviceCollection.cooldownService().setCooldown(control.getModifierKey(), source.getIfPlayer(), Duration.ofSeconds(source.getCooldown()));
    }

}
