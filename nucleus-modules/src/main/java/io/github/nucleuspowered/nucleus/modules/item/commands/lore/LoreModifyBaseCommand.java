/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands.lore;

import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

abstract class LoreModifyBaseCommand implements ICommandExecutor {

    private final Parameter.Value<Integer> loreLine =
            Parameter.builder(Integer.class)
                    .addParser(VariableValueParameters.integerRange().min(1).build())
                    .key("line")
                    .build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.loreLine,
                NucleusParameters.LORE
        };
    }

    /**
     * This method is to update existing lore to the item.
     * When 'editOrInsert' is true, we will edit the lore at the passed line to
     * the passed text. When false, we will rather insert the lore at the specified
     * line.
     *
     * @param context The player attempting to alter an item's lore
     * @param editOrInsert True to edit, false to insert
     * @return The result of the operation
     */
    ICommandResult setLore(final ICommandContext context, final boolean editOrInsert) throws CommandException {
        final Player src = context.getIfPlayer();

        final ItemStack stack = src.itemInHand(HandTypes.MAIN_HAND);
        if (stack.isEmpty()) {
            return context.errorResult("command.lore.set.noitem");
        }

        final List<Component> loreList = stack.get(Keys.LORE).map(ArrayList::new).orElseGet(ArrayList::new);
        final Component getLore = context.requireOne(NucleusParameters.LORE);
        final int line = context.requireOne(this.loreLine) - 1;

        if (editOrInsert) {
            if (loreList.size() < line) {
                return context.errorResult("command.lore.set.invalidEdit");
            }

            loreList.set(line, getLore);
        } else {
            if (loreList.size() < line) {
                loreList.add(getLore);
            } else {
                loreList.add(line, getLore);
            }
        }

        if (stack.offer(Keys.LORE, loreList).isSuccessful()) {
            src.setItemInHand(HandTypes.MAIN_HAND, stack);

            context.sendMessage("command.lore.set.success");
            return context.successResult();
        }

        return context.errorResult("command.lore.set.fail");
    }
}
