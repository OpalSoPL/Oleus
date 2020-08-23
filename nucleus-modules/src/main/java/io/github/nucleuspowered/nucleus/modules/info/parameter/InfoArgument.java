/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.info.parameter;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.io.TextFileController;
import io.github.nucleuspowered.nucleus.modules.info.services.InfoHandler;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class InfoArgument extends CommandElement {

    private final InfoHandler handler;
    private final IMessageProviderService messageProviderService;

    public InfoArgument(@Nullable final TextComponent key, final InfoHandler handler, final INucleusServiceCollection serviceCollection) {
        super(key);
        Preconditions.checkNotNull(handler);
        this.handler = handler;
        this.messageProviderService = serviceCollection.messageProvider();
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        final String a = args.next();
        final Optional<TextFileController> list = this.handler.getSection(a);
        if (list.isPresent()) {
            return new Result(this.handler.getInfoSections().stream().filter(a::equalsIgnoreCase).findFirst().get(), list.get());
        }

        throw args.createError(this.messageProviderService.getMessageFor(source, "args.info.noinfo", a));
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        try {
            final String p = args.peek();
            return this.handler.getInfoSections().stream().filter(x -> x.toLowerCase().startsWith(p.toLowerCase())).collect(Collectors.toList());
        } catch (final Exception e) {
            return new ArrayList<>(this.handler.getInfoSections());
        }
    }

    public static class Result {
        public final String name;
        public final TextFileController text;

        public Result(final String name, final TextFileController text) {
            this.name = name;
            this.text = text;
        }
    }
}
