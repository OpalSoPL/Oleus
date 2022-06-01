/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.craftinggui.commands;

import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.function.Supplier;

public abstract class BasicCraftingCommand implements ICommandExecutor {

    protected abstract Supplier<ContainerType> getArchetype();

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        context.getIfPlayer()
                .openInventory(ViewableInventory.builder().type(this.getArchetype()).completeStructure()
                        .plugin(context.getServiceCollection().pluginContainer()).build())
                .orElseThrow(() -> context.createException("command.crafting.error"));
        return context.successResult();
    }
}
