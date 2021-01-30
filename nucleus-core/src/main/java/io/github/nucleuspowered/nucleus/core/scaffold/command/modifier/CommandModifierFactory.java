/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.command.modifier;

import io.github.nucleuspowered.nucleus.core.scaffold.command.control.CommandControl;
import org.spongepowered.api.ResourceKey;

import java.util.function.Function;

public abstract class CommandModifierFactory implements Function<CommandControl, ICommandModifier> {

    public static class Simple extends CommandModifierFactory {

        private final ResourceKey key;
        private final Function<CommandControl, ICommandModifier> modifierFunction;

        public Simple(final String key, final ICommandModifier modifier) {
            this(ResourceKey.resolve(key), control -> modifier);
        }

        public Simple(final ResourceKey key, final Function<CommandControl, ICommandModifier> modifierFunction) {
            this.key = key;
            this.modifierFunction = modifierFunction;
        }

        public ResourceKey getKey() {
            return this.key;
        }

        @Override public ICommandModifier apply(final CommandControl control) {
            return this.modifierFunction.apply(control);
        }
    }
}
