/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.commandelement;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class LocaleElement extends CommandElement {

    private final INucleusServiceCollection serviceCollection;

    public LocaleElement(final Text key, final INucleusServiceCollection serviceCollection) {
        super(key);
        this.serviceCollection = serviceCollection;
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        final String s = args.next();
        return this.serviceCollection.messageProvider().getLocaleFromName(s).orElseGet(() -> Locale.forLanguageTag(s.replace("_", "-")));
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        final List<String> l = this.serviceCollection.messageProvider().getAllLocaleNames();
        try {
            final String a = args.peek().toLowerCase();
            return l.stream()
                    .filter(x -> x.toLowerCase().startsWith(a))
                    .collect(Collectors.toList());
        } catch (final ArgumentParseException e) {
            return l;
        }
    }
}
