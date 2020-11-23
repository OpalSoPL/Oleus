/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.lists;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AvailableBaseCommand<T extends CatalogType> implements ICommandExecutor {

    private final Class<T> catalogType;
    private final String titleKey;

    AvailableBaseCommand(final Class<T> catalogType, final String titleKey) {
        this.catalogType = catalogType;
        this.titleKey = titleKey;
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {

        final List<Component> types = Sponge.getRegistry().getCatalogRegistry().streamAllOf(this.catalogType)
                .map(x -> context.getMessage("command.world.presets.item", x.getKey().asString(), this.retrieveName(x)))
                .collect(Collectors.toList());

        Util.getPaginationBuilder(context.getAudience())
                .title(context.getMessage(this.titleKey))
                .contents(types)
                .sendTo(context.getAudience());

        return context.successResult();
    }

    protected abstract Component retrieveName(final T type);
}
