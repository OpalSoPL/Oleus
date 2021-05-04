/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.command.parameter.managed.ValueParameterModifier;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public final class UUIDParameterModifier<T> implements ValueParameterModifier<UUID> {

    private final Function<UUID, Optional<T>> validator;
    private final IMessageProviderService messageProvider;

    public static UUIDParameterModifier<GameProfile> gameProfile(final IMessageProviderService messageProvider) {
        return new UUIDParameterModifier<>(x -> Sponge.server().gameProfileManager().cache().findById(x), messageProvider);
    }

    public static UUIDParameterModifier<User> user(final IMessageProviderService messageProvider) {
        return new UUIDParameterModifier<>(x -> Sponge.server().userManager().find(x), messageProvider);
    }

    public static UUIDParameterModifier<ServerPlayer> player(final IMessageProviderService messageProvider) {
        return new UUIDParameterModifier<>(x -> Sponge.server().player(x), messageProvider);
    }

    public UUIDParameterModifier(@Nullable final Function<UUID, Optional<T>> validator, final IMessageProviderService messageProviderService) {
        this.validator = Objects.requireNonNull(validator);
        this.messageProvider = messageProviderService;
    }

    @Override
    public Optional<? extends UUID> modifyResult(Parameter.Key<? super UUID> parameterKey, ArgumentReader.Immutable reader, CommandContext.Builder context, @Nullable UUID value) throws ArgumentParseException {
        if (value == null) {
            throw reader.createException(Component.text("UUID cannot be null"));
        }
        try {
            this.validator.apply(value).orElseThrow(() ->
                    reader.createException(this.messageProvider.getMessageFor(context.cause().audience(), "args.uuid.notvalid.nomatch")));
            return Optional.of(value);
        } catch (final IllegalArgumentException e) {
            throw reader.createException(this.messageProvider.getMessageFor(context.cause().audience(), "args.uuid.notvalid.malformed"));
        }
    }

}
