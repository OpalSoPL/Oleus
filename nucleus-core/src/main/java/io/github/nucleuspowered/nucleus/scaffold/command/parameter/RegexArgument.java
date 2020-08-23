/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.util.TriFunction;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class RegexArgument extends CommandElement {

    private final Pattern regex;
    private final String errorKey;
    private final TriFunction<CommandSource, CommandArgs, CommandContext, List<String>> function;
    private final IMessageProviderService messageProvider;

    public RegexArgument(@Nullable final TextComponent key, final String regex, final String errorKey, final INucleusServiceCollection serviceCollection) {
        this(key, regex, errorKey, null, serviceCollection);
    }

    public RegexArgument(@Nullable final TextComponent key, final String regex, final String errorKey, @Nullable final TriFunction<CommandSource, CommandArgs, CommandContext,
            List<String>> tabComplete, final INucleusServiceCollection serviceCollection) {
        super(key);

        Preconditions.checkNotNull(regex);
        Preconditions.checkNotNull(errorKey);

        this.regex = Pattern.compile(regex);
        this.errorKey = errorKey;
        this.function = tabComplete == null ? (a, b, c) -> Lists.newArrayList() : tabComplete;
        this.messageProvider = serviceCollection.messageProvider();
    }

    @Nullable @Override protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        final String arg = args.next();
        if (this.regex.matcher(arg).matches()) {
            return arg;
        }

        throw args.createError(this.messageProvider.getMessageFor(source, this.errorKey));
    }

    @Override public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        return this.function.accept(src, args, context);
    }
}
