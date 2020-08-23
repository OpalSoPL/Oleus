/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.scaffold.command.parameter.util.WrappedElement;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

public class AlternativeUsageArgument extends WrappedElement {

    private final Function<CommandSource, Text> usage;

    public AlternativeUsageArgument(final CommandElement wrappedElement, final Function<CommandSource, Text> usage) {
        super(wrappedElement);
        this.usage = usage;
    }

    @Nullable @Override protected Object parseValue(final CommandSource source, final CommandArgs args) {
        return null;
    }

    @Override public void parse(final CommandSource source, final CommandArgs args, final CommandContext context) throws ArgumentParseException {
        this.getWrappedElement().parse(source, args, context);
    }

    @Override public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        return this.getWrappedElement().complete(src, args, context);
    }

    @Override public TextComponent getUsage(final CommandSource src) {
        return this.usage.apply(src);
    }
}
