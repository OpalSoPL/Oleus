/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.modifier;

import io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl.CooldownModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl.CostModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl.RequiresEconomyModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.impl.WarmupModifier;
import io.github.nucleuspowered.nucleus.scaffold.registry.NucleusRegistryModule;
public class CommandModiferRegistry extends NucleusRegistryModule<CommandModifierFactory> {

    @Override public Class<CommandModifierFactory> catalogClass() {
        return CommandModifierFactory.class;
    }

    @Override public void registerModuleDefaults() {
        this.registerAdditionalCatalog(new CommandModifierFactory.Simple(new CooldownModifier()));
        this.registerAdditionalCatalog(new CommandModifierFactory.Simple(new CostModifier()));
        this.registerAdditionalCatalog(new CommandModifierFactory.Simple(new WarmupModifier()));
        this.registerAdditionalCatalog(new CommandModifierFactory.Simple(new RequiresEconomyModifier()));
    }
}
