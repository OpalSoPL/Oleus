/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public final class UUIDParameter<T> implements ValueParameter<T> {

    private final Function<UUID, Optional<T>> validator;
    private final IMessageProviderService messageProvider;

    public static UUIDParameter<GameProfile> gameProfile(final IMessageProviderService messageProvider) {
        return new UUIDParameter<>(x -> Sponge.getServer().getGameProfileManager().getCache().getById(x), messageProvider);
    }

    public static UUIDParameter<User> user(final IMessageProviderService messageProvider) {
        return new UUIDParameter<>(x -> Sponge.getServer().getUserManager().get(x), messageProvider);
    }

    public static UUIDParameter<ServerPlayer> player(final IMessageProviderService messageProvider) {
        return new UUIDParameter<>(x -> Sponge.getServer().getPlayer(x), messageProvider);
    }

    public UUIDParameter(@Nullable final Function<UUID, Optional<T>> validator, final IMessageProviderService messageProviderService) {
        this.validator = Objects.requireNonNull(validator);
        this.messageProvider = messageProviderService;
    }

    @Override
    public List<String> complete(final CommandContext context, final String currentInput) {
        return Collections.emptyList();
    }

    @Override
    public Optional<? extends T> getValue(final Parameter.Key<? super T> parameterKey, final ArgumentReader.Mutable reader, final CommandContext.Builder context)
            throws ArgumentParseException {
        String a = reader.parseString();
        try {
            if (!a.contains("-") && a.matches("[0-9a-f]{32}")) {
                a = String.format("%s-%s-%s-%s-%s", a.substring(0, 8), a.substring(8, 12), a.substring(12, 16), a.substring(16, 20), a.substring(20));
            }

            final UUID uuid = UUID.fromString(a);
            final T result = this.validator.apply(uuid).orElseThrow(() ->
                    reader.createException(this.messageProvider.getMessageFor(context.getCause().getAudience(), "args.uuid.notvalid.nomatch")));
            return Optional.of(result);
        } catch (final IllegalArgumentException e) {
            throw reader.createException(this.messageProvider.getMessageFor(context.getCause().getAudience(), "args.uuid.notvalid.malformed"));
        }
    }
}
