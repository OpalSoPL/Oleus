/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.parameter;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class GameProfileArgument extends CommandElement {

    private final Pattern p = Pattern.compile("[a-zA-Z0-9_]{1,16}");
    private final IMessageProviderService messageProviderService;

    public GameProfileArgument(@Nullable final Text key, final INucleusServiceCollection nucleusServiceCollection) {
        super(key);
        this.messageProviderService = nucleusServiceCollection.messageProvider();
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        final String name = args.next();
        if (!this.p.matcher(name).matches()) {
            throw args.createError(this.messageProviderService.getMessageFor(source, "args.gameprofile.format"));
        }

        final List<GameProfile> lgp = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).getAll()
                .stream().filter(x -> x.getName().isPresent() && x.getName().get().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        if (lgp.isEmpty()) {
            throw args.createError(this.messageProviderService.getMessageFor(source, "args.gameprofile.none", name));
        }

        if (lgp.size() == 1) {
            return lgp.get(0);
        }

        return lgp;
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        try {
            final String arg = args.peek().toLowerCase();
            final List<String> onlinePlayers = Sponge.getServer().getOnlinePlayers().stream().map(User::getName).collect(Collectors.toList());
            return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).getAll()
                .stream().filter(x -> x.getName().isPresent() && x.getName().get().toLowerCase().startsWith(arg))
                .map(x -> x.getName().get())
                .sorted((first, second) -> {
                    final boolean firstBool = onlinePlayers.contains(first);
                    final boolean secondBool = onlinePlayers.contains(second);
                    if (firstBool == secondBool) {
                        return first.compareTo(second);
                    }

                    return firstBool ? -1 : 1;
                })
                .collect(Collectors.toList());
        } catch (final ArgumentParseException e) {
            return new ArrayList<>();
        }
    }
}
