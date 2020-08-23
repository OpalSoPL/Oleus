/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.craftinggui;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
public abstract class BasicCraftingCommand implements ICommandExecutor {

    protected abstract InventoryArchetype getArchetype();

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Inventory i = Inventory.builder().of(getArchetype()).build(context.getServiceCollection().pluginContainer());
        context.getCommandSourceRoot().openInventory(i).orElseThrow(() -> context.createException("command.crafting.error"));
        return context.successResult();
    }
}
