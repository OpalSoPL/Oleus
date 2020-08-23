/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

public class UUIDArgument<T> extends CommandElement {

    @Nullable private final Function<UUID, Optional<T>> validator;
    private final IMessageProviderService messageProvider;

    public static UUIDArgument<GameProfile> gameProfile(final TextComponent key, final INucleusServiceCollection serviceCollection) {
        return new UUIDArgument<>(key, x -> Sponge.getServiceManager().provideUnchecked(UserStorageService.class).getAll()
                .stream().filter(y -> y.getUniqueId().equals(x)).findFirst(), serviceCollection);
    }

    public static UUIDArgument<User> user(final TextComponent key, final INucleusServiceCollection serviceCollection) {
        return new UUIDArgument<>(key, x -> Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(x), serviceCollection);
    }

    public static UUIDArgument<Player> player(final TextComponent key, final INucleusServiceCollection serviceCollection) {
        return new UUIDArgument<>(key, x -> Sponge.getServer().getPlayer(x), serviceCollection);
    }

    public UUIDArgument(@Nullable final TextComponent key, @Nullable final Function<UUID, Optional<T>> validator, final INucleusServiceCollection serviceCollection) {
        super(key);
        this.validator = validator;
        this.messageProvider = serviceCollection.messageProvider();
    }

    @Nullable @Override protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        String a = args.next().toLowerCase();
        try {
            if (!a.contains("-") && a.matches("[0-9a-f]{32}")) {
                a = String.format("%s-%s-%s-%s-%s", a.substring(0, 8), a.substring(8, 12), a.substring(12, 16), a.substring(16, 20), a.substring(20));
            }

            final UUID uuid = UUID.fromString(a);
            if (this.validator != null) {
                return this.validator.apply(uuid).orElseThrow(() ->
                    args.createError(this.messageProvider.getMessageFor(source, "args.uuid.notvalid.nomatch")));
            }

            return uuid;
        } catch (final IllegalArgumentException e) {
            throw args.createError(this.messageProvider.getMessageFor(source, "args.uuid.notvalid.malformed"));
        }
    }

    @Override public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        return Lists.newArrayList();
    }
}
