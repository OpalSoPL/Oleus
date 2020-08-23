/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter.util;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public abstract class WrappedElement extends CommandElement {

    private final CommandElement wrappedElement;

    public WrappedElement(final CommandElement wrappedElement) {
        super(wrappedElement.getKey());
        this.wrappedElement = wrappedElement;
    }

    public CommandElement getWrappedElement() {
        return this.wrappedElement;
    }

    @Nullable @Override public TextComponent getKey() {
        return this.wrappedElement.getKey();
    }

    @Override public TextComponent getUsage(final CommandSource src) {
        return this.getWrappedElement().getUsage(src);
    }
}
