/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.item.commands;

import io.github.nucleuspowered.nucleus.modules.item.ItemPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.scaffold.command.modifier.CommandModifiers;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.vavr.collection.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EssentialsEquivalent({"enchant", "enchantment"})
@Command(
        aliases = { "enchant", "enchantment" },
        basePermission = ItemPermissions.BASE_ENCHANT,
        commandDescriptionKey = "enchant",
        modifiers = {
                @CommandModifier(value = CommandModifiers.HAS_COOLDOWN, exemptPermission = ItemPermissions.EXEMPT_COOLDOWN_ENCHANT),
                @CommandModifier(value = CommandModifiers.HAS_WARMUP, exemptPermission = ItemPermissions.EXEMPT_WARMUP_ENCHANT),
                @CommandModifier(value = CommandModifiers.HAS_COST, exemptPermission = ItemPermissions.EXEMPT_COST_ENCHANT)
        },
        associatedPermissions = ItemPermissions.ENCHANT_UNSAFE
)
public class EnchantCommand implements ICommandExecutor {

    private final Parameter.Value<EnchantmentType> enchantmentType = Parameter.builder(EnchantmentType.class)
            .addParser(VariableValueParameters.registryEntryBuilder(RegistryTypes.ENCHANTMENT_TYPE).defaultNamespace("minecraft").build())
            .key("enchantment")
            .build();
    private final Parameter.Value<Integer> level = Parameter.builder(Integer.class)
            .addParser(VariableValueParameters.integerRange().min(0).max((int) Short.MAX_VALUE).build())
            .key("level")
            .build();

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.builder().setRequirement(x -> serviceCollection.permissionService().hasPermission(x, ItemPermissions.ENCHANT_UNSAFE))
                    .alias("u")
                    .alias("unsafe")
                    .build(),
                Flag.of("o", "overwrite")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                this.enchantmentType,
                this.level
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Player src = context.getIfPlayer();
        // Check for item in hand
        final ItemStack itemInHand = src.itemInHand(HandTypes.MAIN_HAND);
        if (itemInHand.isEmpty()) {
            return context.errorResult("command.enchant.noitem");
        }

        // Get the arguments
        final EnchantmentType enchantment = context.requireOne(this.enchantmentType);
        final int level = context.requireOne(this.level);
        final boolean allowUnsafe = context.hasFlag("u");
        final boolean allowOverwrite = context.hasFlag("o");

        // Can we apply the enchantment?
        if (!allowUnsafe) {
            if (!enchantment.canBeAppliedToStack(itemInHand)) {
                return context.errorResult("command.enchant.nounsafe.enchant", itemInHand);
            }

            if (level > enchantment.maximumLevel()) {
                return context.errorResult("command.enchant.nounsafe.level", itemInHand);
            }
        }

        // We know this should exist.
        final List<Enchantment> enchantments = itemInHand.get(Keys.APPLIED_ENCHANTMENTS).orElseGet(Collections::emptyList);

        final List<Enchantment> enchantsToSet;
        if (level == 0) {
            // we want to remove only.
            enchantsToSet = Stream.ofAll(enchantments).filter(x -> !x.type().equals(enchantment)).asJava();
            if (enchantsToSet.size() == enchantments.size()) {
                return context.errorResult("command.enchant.noenchantment", enchantment);
            }
        } else {

            final Stream<Enchantment> toRemove = Stream.ofAll(enchantments)
                    .filter(x -> x.type().isCompatibleWith(enchantment) || x.type().equals(enchantment));

            if (!allowOverwrite && toRemove.isEmpty()) {
                // Build the list of the enchantment names, and send it.
                return context.errorResult("command.enchant.overwrite",
                        Component.join(JoinConfiguration.commas(true), toRemove.map(Enchantment::type).toJavaList()));
            }

            enchantsToSet = new ArrayList<>(enchantments);
            for (final Enchantment r : toRemove) {
                enchantsToSet.remove(r);
            }

            // Create the enchantment
            enchantsToSet.add(Enchantment.of(enchantment, level));
        }

        // Offer it to the item.
        final DataTransactionResult dtr = itemInHand.offer(Keys.APPLIED_ENCHANTMENTS, enchantsToSet);
        if (dtr.isSuccessful()) {
            // If successful, we need to put the item in the player's hand for it to actually take effect.
            src.setItemInHand(HandTypes.MAIN_HAND, itemInHand);
            if (level == 0) {
                context.sendMessage("command.enchant.removesuccess", enchantment);
            } else {
                context.sendMessage("command.enchant.success", enchantment, level);
            }
            return context.successResult();
        }

        return context.errorResult("command.enchant.error", enchantment, level);
    }
}
