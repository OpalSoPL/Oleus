/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.parameters;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.api.module.home.data.Home;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import io.github.nucleuspowered.nucleus.modules.home.services.HomeService;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * Returns a {@link NamedLocation}
 */
public class HomeArgument extends CommandElement {

    private final HomeService homeService;
    protected final IMessageProviderService messageProviderService;

    public HomeArgument(@Nullable final TextComponent key, final HomeService homeService, final IMessageProviderService messageProviderService) {
        super(key);
        this.homeService = homeService;
        this.messageProviderService = messageProviderService;
    }

    @Nullable
    @Override
    protected Object parseValue(final CommandSource source, final CommandArgs args) throws ArgumentParseException {
        if (!(source instanceof User)) {
            throw args.createError(this.messageProviderService.getMessageFor(source, "command.playeronly"));
        }

        return getHome((User) source, source, args.next(), args);
    }

    Home getHome(final User user, final CommandSource source, final String home, final CommandArgs args) throws ArgumentParseException {
        try {
            final Optional<Home> owl = this.homeService.getHome(user.getUniqueId(), home);
            if (owl.isPresent()) {
                return owl.get();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw args.createError(Text.of(TextColors.RED, "An unspecified error occurred"));
        }

        throw args.createError(this.messageProviderService.getMessageFor(source, "args.home.nohome", home));
    }

    @Override
    public List<String> complete(final CommandSource src, final CommandArgs args, final CommandContext context) {
        if (!(src instanceof User)) {
            return Lists.newArrayList();
        }

        final User u = (User) src;
        try {
            return complete(u, args.peek());
        } catch (final ArgumentParseException e) {
            return complete(u, "");
        }
    }

    protected List<String> complete(final User src, final String homeName) {
        final Collection<String> s;
        try {
            s = this.homeService.getHomeNames(src.getUniqueId());
        } catch (final Exception e) {
            return Lists.newArrayList();
        }

        final String name = homeName.toLowerCase();
        return s.stream().filter(x -> x.toLowerCase().startsWith(name)).limit(20).collect(Collectors.toList());
    }
}
