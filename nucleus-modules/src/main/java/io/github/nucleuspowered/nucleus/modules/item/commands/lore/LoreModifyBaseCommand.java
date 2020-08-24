/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands.lore;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import java.util.List;

abstract class LoreModifyBaseCommand implements ICommandExecutor {

    final String loreLine = "line";

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[]{
                new PositiveIntegerArgument(Text.of(this.loreLine), false, serviceCollection),
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
     * @param message The text to offer to the item
     * @param line The line of the lore we want to edit
     * @param editOrInsert True to edit, false to insert
     * @return The result of the operation
     */
    ICommandResult setLore(final ICommandContext context, final String message, int line, final boolean editOrInsert) throws CommandException {
        final Player src = context.getIfPlayer();
        // The number will come in one based - we need to reduce by one.
        line--;

        final ItemStack stack = src.getItemInHand(HandTypes.MAIN_HAND).orElseThrow(() -> context.createException("command.lore.set.noitem"));
        final LoreData loreData = stack.getOrCreate(LoreData.class).get();

        final TextComponent getLore = TextSerializers.FORMATTING_CODE.deserialize(message);

        final List<Text> loreList = loreData.lore().get();
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

        if (stack.offer(Keys.ITEM_LORE, loreList).isSuccessful()) {
            src.setItemInHand(HandTypes.MAIN_HAND, stack);

            context.sendMessage("command.lore.set.success");
            return context.successResult();
        }

        return context.errorResult("command.lore.set.fail");
    }
}
