/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class RequiresEconomyModifier implements ICommandModifier {

    @Override
    public Optional<Component> testRequirement(final ICommandContext source, final CommandControl control,
            final INucleusServiceCollection serviceCollection, final CommandModifier modifier) {
        if (!serviceCollection.economyServiceProvider().serviceExists()) {
            return Optional.of(serviceCollection.messageProvider().getMessageFor(source.getCause().getAudience(), "command.economyrequired"));
        }

        return Optional.empty();
    }

}
