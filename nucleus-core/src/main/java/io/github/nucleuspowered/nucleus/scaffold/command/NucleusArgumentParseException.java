/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command;

import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;

public class NucleusArgumentParseException extends ArgumentParseException {

    private final IMessageProviderService messageProviderService;
    @Nullable private final TextComponent subcommands;
    @Nullable private final TextComponent usage;
    private final boolean isEnd;

    public static NucleusArgumentParseException from(
            final IMessageProviderService messageProviderService,
            final ArgumentParseException exception,
            @Nullable final TextComponent usage,
            @Nullable final TextComponent subcommands) {
        return new NucleusArgumentParseException(
                messageProviderService,
                Text.of(TextColors.RED, exception.getMessage()),
                "",
                exception.getPosition(),
                usage,
                subcommands,
                exception instanceof NucleusArgumentParseException && ((NucleusArgumentParseException) exception).isEnd()
        );
    }

    public NucleusArgumentParseException(
            final IMessageProviderService messageProviderService,
            final TextComponent message,
            final String source,
            final int position,
            @Nullable final TextComponent usage,
            @Nullable final TextComponent subcommands,
            final boolean isEnd) {
        super(message, source, position);
        this.messageProviderService = messageProviderService;
        this.usage = usage;
        this.subcommands = subcommands;
        this.isEnd = isEnd;
    }

    @Override public TextComponent getText() {
        final TextComponent t = super.getText();
        if (this.usage == null && this.subcommands == null) {
            return t;
        }

        return Text.join(t, Text.NEW_LINE, this.getUsage());
    }

    @Nullable public TextComponent getUsage() {
        final Text.Builder builder = Text.builder();
        if (this.usage != null) {
            builder.append(Text.NEW_LINE).append(this.messageProviderService.getMessage("command.exception.usage", this.usage));
        }

        if (this.subcommands != null) {
            builder.append(Text.NEW_LINE).append(this.messageProviderService.getMessage("command.exception.subcommands", this.subcommands));
        }

        return builder.build();
    }

    @Override public boolean shouldIncludeUsage() {
        return false;
    }

    public boolean isEnd() {
        return this.isEnd;
    }
}
