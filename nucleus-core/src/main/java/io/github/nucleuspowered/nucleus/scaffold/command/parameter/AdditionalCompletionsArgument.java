/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.util.WrappedElement;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;

import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

public class AdditionalCompletionsArgument extends WrappedElement {

    private final BiFunction<CommandSource, String, List<String>> additional;
    private final int minArgs;
    private final int maxArgs;

    public AdditionalCompletionsArgument(final CommandElement wrapped, final int min, final int max, final BiFunction<CommandSource, String, List<String>> additional) {
        super(wrapped);
        this.additional = additional;
        this.maxArgs = max;
        this.minArgs = min;
    }


    @Nullable @Override protected Object parseValue(final CommandSource source, final CommandArgs args) {
        return null;
    }

    @Override public void parse(final CommandSource source, final CommandArgs args, final CommandContext context) throws ArgumentParseException {
        this.getWrappedElement().parse(source, args, context);
    }

    @Override public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        final List<String> s = this.getWrappedElement().complete(src, args, context);

        if (args.getAll().size() >= this.minArgs && args.getAll().size() <= this.maxArgs) {
            try {
                final String a = args.peek();
                final List<String> result = Lists.newArrayList(s);
                result.addAll(this.additional.apply(src, a));
                return result;
            } catch (final ArgumentParseException e) {
                // ignored
            }
        }

        return s;
    }
}
