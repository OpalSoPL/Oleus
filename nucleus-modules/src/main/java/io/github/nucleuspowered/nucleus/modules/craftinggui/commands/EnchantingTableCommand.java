/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.craftinggui.commands;

import io.github.nucleuspowered.nucleus.modules.craftinggui.CraftingGuiPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;

import java.util.function.Supplier;

@Command(
        aliases = {"enchantingtable", "enchanttable", "etable"},
        basePermission = CraftingGuiPermissions.BASE_ENCHANTINGTABLE,
        commandDescriptionKey = "enchantingtable",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = CraftingGuiPermissions.EXEMPT_COOLDOWN_ENCHANTINGTABLE),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = CraftingGuiPermissions.EXEMPT_WARMUP_ENCHANTINGTABLE),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = CraftingGuiPermissions.EXEMPT_COST_ENCHANTINGTABLE)
        }
)
public class EnchantingTableCommand extends BasicCraftingCommand {

    @Override
    protected Supplier<ContainerType> getArchetype() {
        // Max power is 32, when we can implement it.
        return ContainerTypes.ENCHANTMENT;
    }


}
