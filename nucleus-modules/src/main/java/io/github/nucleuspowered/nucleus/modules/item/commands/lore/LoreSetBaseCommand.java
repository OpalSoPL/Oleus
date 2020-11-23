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
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

abstract class LoreSetBaseCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.LORE
        };
    }

    ICommandResult setLore(final ICommandContext context, final boolean replace) throws CommandException {
        final Player src = context.getIfPlayer();
        final ItemStack stack = src.getItemInHand(HandTypes.MAIN_HAND);
        if (stack.isEmpty()) {
            return context.errorResult("command.lore.set.noitem");
        }
        final List<Component> components = stack.get(Keys.LORE).map(ArrayList::new).orElseGet(ArrayList::new);
        final Component getLore = context.requireOne(NucleusParameters.LORE);

        if (replace) {
            components.clear();
        }
        components.add(getLore);

        if (stack.offer(Keys.LORE, components).isSuccessful()) {
            src.setItemInHand(HandTypes.MAIN_HAND, stack);

            context.sendMessage("command.lore.set.success");
            return context.successResult();
        }

        return context.errorResult("command.lore.set.fail");
    }
}
