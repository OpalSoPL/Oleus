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
        aliases = "anvil",
        basePermission = CraftingGuiPermissions.BASE_ANVIL,
        commandDescriptionKey = "anvil",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = CraftingGuiPermissions.EXEMPT_COOLDOWN_ANVIL),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = CraftingGuiPermissions.EXEMPT_WARMUP_ANVIL),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = CraftingGuiPermissions.EXEMPT_COST_ANVIL)
        }
)
public class AnvilCommand extends BasicCraftingCommand {

    @Override
    protected Supplier<ContainerType> getArchetype() {
        return ContainerTypes.ANVIL;
    }

    @Override
    protected String titleKey() {
        return "command.anvil.gui.title";
    }
}
