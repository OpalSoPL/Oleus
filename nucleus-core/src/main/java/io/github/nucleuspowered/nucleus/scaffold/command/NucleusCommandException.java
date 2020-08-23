/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command;

import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * Wrapper exception that contains a list of exceptions to pass down that may have been caused during execution.
 */
public class NucleusCommandException extends CommandException {

    private final boolean allowFallback;
    private final List<Tuple<String, CommandException>> exceptions;
    private final IMessageProviderService messageProvider;
    private Boolean overrideUsage = null;

    public NucleusCommandException(final List<Tuple<String, CommandException>> exception, final boolean allowFallback, final IMessageProviderService messageProviderService) {
        super(Text.EMPTY);
        this.exceptions = exception;
        this.allowFallback = allowFallback;
        this.messageProvider = messageProviderService;
    }

    public List<Tuple<String, CommandException>> getExceptions() {
        return this.exceptions;
    }

    @Nullable @Override public TextComponent getText() {
        if (this.exceptions.isEmpty()) {
            // Unable to get the error.
            return this.messageProvider.getMessage("command.exception.nomoreinfo");
        }


        // Is it only command permission exceptions?
        if (this.exceptions.stream().allMatch(x -> x.getSecond() instanceof CommandPermissionException)) {
            return this.exceptions.get(0).getSecond().getText();
        }

        if (this.exceptions.stream().allMatch(x -> {
            final CommandException e = x.getSecond();
            return e instanceof NucleusArgumentParseException && ((NucleusArgumentParseException) e).isEnd();
        })) {
            if (this.exceptions.size() == 1) {
                final Tuple<String, CommandException> exceptionTuple = this.exceptions.get(0);
                return Text.of( this.messageProvider.getMessage("command.exception.fromcommand", exceptionTuple.getFirst()),
                        Text.NEW_LINE, TextColors.RED, exceptionTuple.getSecond().getText());
            } else {
                return this.print(this.exceptions);
            }
        }

        final List<Tuple<String, CommandException>> lce = this.exceptions.stream()
                .filter(x -> {
                    final CommandException e = x.getSecond();
                    return !(e instanceof NucleusArgumentParseException) || !((NucleusArgumentParseException) e).isEnd();
                })
                .filter(x -> !CommandPermissionException.class.isInstance(x))
                .collect(Collectors.toList());
        if (lce.size() == 1) {
            final Tuple<String, CommandException> exceptionTuple = this.exceptions.get(0);
            return Text.of(this.messageProvider.getMessage("command.exception.fromcommand", exceptionTuple.getFirst()),
                    Text.NEW_LINE, TextColors.RED, exceptionTuple.getSecond().getText());
        }

        return this.print(lce);
    }

    private TextComponent print(final List<Tuple<String, CommandException>> lce) {
        final TextComponent sept = this.messageProvider.getMessage("command.exception.separator");
        final Text.Builder builder = this.messageProvider.getMessage("command.exception.multiple")
                .toBuilder();
        lce.forEach(x -> builder.append(Text.NEW_LINE).append(sept)
                .append(Text.NEW_LINE)
                .append(this.messageProvider.getMessage("command.exception.fromcommand", x.getFirst()))
                .append(Text.NEW_LINE)
                .append(x.getSecond().getText()));

        builder.append(Text.NEW_LINE).append(sept);

        return builder.toText();
    }

    public void setOverrideUsage(final Boolean overrideUsage) {
        this.overrideUsage = overrideUsage;
    }

    @Override public boolean shouldIncludeUsage() {
        return this.overrideUsage == null ? super.shouldIncludeUsage() : this.overrideUsage;
    }

    public boolean isAllowFallback() {
        return this.allowFallback;
    }
}
