/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.selector.Selector;

import java.util.List;

import javax.annotation.Nullable;

public class SelectorArgument extends CommandElement {

    private final Class<? extends Entity> selectorFilter;
    private final CommandElement wrapped;
    private final IMessageProviderService messageProvider;

    public SelectorArgument(final CommandElement wrapped, final Class<? extends Entity> selectorFilter, final INucleusServiceCollection serviceCollection) {
        super(wrapped.getKey());
        this.wrapped = wrapped;
        this.selectorFilter = selectorFilter;
        this.messageProvider = serviceCollection.messageProvider();
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        return null;
    }

    @Override
    public void parse(final CommandSource source, final CommandArgs args, final CommandContext context) throws ArgumentParseException {
        final String a = args.peek();
        if (a.startsWith("@")) {
            try {
                // Time to try to eek it all out.
                Selector.parse(a).resolve(source).stream().filter(this.selectorFilter::isInstance)
                        .forEach(x -> context.putArg(getKey(), x));
            } catch (final IllegalArgumentException e) {
                throw args.createError(Text.of(e.getMessage()));
            }

            args.next();
            if (context.hasAny(getKey())) {
                return;
            }

            throw args.createError(this.messageProvider.getMessageFor(source, "args.selector.notarget"));
        }

        this.wrapped.parse(source, args, context);
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        return this.wrapped.complete(src, args, context);
    }

    @Override
    public Text getUsage(final CommandSource src) {
        return this.wrapped.getUsage(src);
    }

}
