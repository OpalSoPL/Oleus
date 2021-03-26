/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands.lore;

import io.github.nucleuspowered.nucleus.modules.item.ItemPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
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
import java.util.Collections;
import java.util.List;

@Command(
        aliases = { "delete" },
        basePermission = ItemPermissions.BASE_LORE_SET,
        commandDescriptionKey = "lore.delete",
        parentCommand = LoreCommand.class,
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = ItemPermissions.EXEMPT_COOLDOWN_LORE_SET),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = ItemPermissions.EXEMPT_WARMUP_LORE_SET),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = ItemPermissions.EXEMPT_COST_LORE_SET)
        }
)
public class LoreDeleteCommand implements ICommandExecutor {

    private final Parameter.Value<Integer> loreLine =
            Parameter.builder(Integer.class)
                    .addParser(VariableValueParameters.integerRange().min(0).build())
                    .key("line")
                    .build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.loreLine
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player src = context.getIfPlayer();
        final int line = context.requireOne(this.loreLine) - 1;

        final ItemStack stack = src.itemInHand(HandTypes.MAIN_HAND);
        if (stack.isEmpty()) {
            return context.errorResult("command.lore.clear.noitem");
        }

        final List<Component> loreList = stack.get(Keys.LORE).orElseGet(Collections::emptyList);
        if (loreList.size() < line) {
            return context.errorResult("command.lore.set.invalidLine");
        }

        final List<Component> components = new ArrayList<>(loreList);
        components.remove(line);

        if (stack.offer(Keys.LORE, components).isSuccessful()) {
            src.setItemInHand(HandTypes.MAIN_HAND, stack);

            context.sendMessage("command.lore.set.success");
            return context.successResult();
        }

        return context.errorResult("command.lore.set.fail");
    }
}
