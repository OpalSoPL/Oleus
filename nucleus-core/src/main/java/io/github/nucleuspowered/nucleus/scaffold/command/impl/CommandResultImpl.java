/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.impl;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CommandResultImpl implements ICommandResult {

    public static final ICommandResult SUCCESS = new CommandResultImpl(true, false);

    public static final ICommandResult WILL_CONTINUE = new CommandResultImpl(false, true);

    public static final ICommandResult FAILURE = new CommandResultImpl(false, false);

    @Nullable private final String key;
    @Nullable private final Object[] args;
    private final boolean success;
    private final boolean willContinue;
    private final IMessageProviderService messageProviderService;

    private CommandResultImpl(final boolean success, final boolean willContinue) {
        this(success, willContinue, null, null, null);
    }

    public CommandResultImpl(final IMessageProviderService messageProviderService, final String key, final Object[] args) {
        this(
                false,
                false,
                Preconditions.checkNotNull(messageProviderService),
                Preconditions.checkNotNull(key),
                Preconditions.checkNotNull(args)
        );
    }

    private CommandResultImpl(
            final boolean success,
            final boolean willContinue,
            @Nullable final IMessageProviderService messageProviderService,
            @Nullable final String key,
            @Nullable final Object[] args) {
        this.key = key;
        this.args = args;
        this.messageProviderService = messageProviderService;
        this.success = success;
        this.willContinue = willContinue;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public boolean isWillContinue() {
        return this.willContinue;
    }

    @Override
    public Optional<Text> getErrorMessage(final CommandSource source) {
        if (this.messageProviderService == null) {
            return Optional.empty();
        }

        return Optional.of(this.messageProviderService.getMessageFor(source, this.key, this.args));
    }

    public static class Literal extends CommandResultImpl {

        private final TextComponent literal;

        public Literal(final TextComponent literal) {
            super(false, false);
            this.literal = literal;
        }

        @Override public Optional<Text> getErrorMessage(final CommandSource source) {
            return Optional.of(this.literal);
        }
    }
}
