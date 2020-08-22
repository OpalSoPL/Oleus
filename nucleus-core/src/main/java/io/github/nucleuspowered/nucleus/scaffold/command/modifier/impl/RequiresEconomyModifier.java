/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import java.util.Optional;

public class RequiresEconomyModifier implements ICommandModifier, IReloadableService.Reloadable {
    @Override public String getId() {
        return CommandModifiers.REQUIRE_ECONOMY;
    }

    @Override public String getName() {
        return "Requires Economy Modifier";
    }

    @Nullable private Text lazyLoad = null;

    @Override
    public Optional<Text> testRequirement(final ICommandContext.Mutable<? extends CommandSource> source, final CommandControl control,
            final INucleusServiceCollection serviceCollection, final CommandModifier modifier) throws CommandException {
        if (!serviceCollection.economyServiceProvider().serviceExists()) {
            if (this.lazyLoad == null) {
                this.lazyLoad = serviceCollection.messageProvider().getMessageFor(source.getCommandSource(), "command.economyrequired");
            }

            return Optional.of(this.lazyLoad);
        }

        return Optional.empty();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.lazyLoad = null;
    }
}
