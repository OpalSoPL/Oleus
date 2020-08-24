/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.playername;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.Nameable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class PlayerDisplayNameService implements IPlayerDisplayNameService, IReloadableService.Reloadable {

    private final LinkedHashSet<DisplayNameResolver> resolvers = new LinkedHashSet<>();
    private final LinkedHashSet<DisplayNameQuery> queries = new LinkedHashSet<>();

    private final IMessageProviderService messageProviderService;

    private String commandNameOnClick = null;

    @Inject
    public PlayerDisplayNameService(final INucleusServiceCollection serviceCollection) {
        this.messageProviderService = serviceCollection.messageProvider();
    }

    @Override
    public void provideDisplayNameResolver(final DisplayNameResolver resolver) {
        this.resolvers.add(resolver);
    }

    @Override
    public void provideDisplayNameQuery(final DisplayNameQuery resolver) {
        this.queries.add(resolver);
    }

    @Override
    public Optional<User> getUser(final String displayName) {
        final Optional<User> withRealName = Sponge.getServer().getUserManager().get(displayName);
        if (withRealName.isPresent()) {
            return withRealName;
        }

        for (final DisplayNameQuery query : this.queries) {
            final Optional<User> user = query.resolve(displayName);
            if (user.isPresent()) {
                return user;
            }
        }

        return Optional.empty();
    }

    @Override
    public Map<UUID, List<String>> startsWith(final String displayName) {
        final Map<UUID, List<String>> uuids = new HashMap<>();
        Sponge.getServer().getOnlinePlayers().stream()
            .filter(x -> x.getName().toLowerCase().startsWith(displayName.toLowerCase()))
            .forEach(x -> uuids.put(x.getUniqueId(), Lists.newArrayList(x.getName())));

        for (final DisplayNameQuery query : this.queries) {
            query.startsWith(displayName).forEach(
                    (uuid, name) -> uuids.computeIfAbsent(uuid, x -> new ArrayList<>()).add(name)
            );
        }

        return uuids;
    }

    @Override
    public Optional<User> getUser(final TextComponent displayName) {
        return this.getUser(displayName.toString());
    }

    @Override
    public TextComponent getDisplayName(final UUID playerUUID) {
        if (playerUUID == Util.CONSOLE_FAKE_UUID) {
            return this.getDisplayName(Sponge.getSystemSubject());
        }

        final User user = Sponge.getServer()
                .getUserManager()
                .get(playerUUID)
                .orElseThrow(() -> new IllegalArgumentException("UUID does not map to a player"));
        for (final DisplayNameResolver resolver : this.resolvers) {
            final Optional<TextComponent> userName = resolver.resolve(playerUUID);
            if (userName.isPresent()) {
                return userName
                        .map(x -> this.addHover(x, user.getName()))
                        .get();
            }
        }

        // Set name colours

        return this.addHover(user.get(Keys.DISPLAY_NAME).map(x -> TextComponent.builder().append(x).build())
                .orElseGet(() -> TextComponent.of(user.getName())), user.getName());
    }

    @Override
    public TextComponent getDisplayName(final Audience source) {
        if (source instanceof SystemSubject || source instanceof Server) {
            return this.getDisplayName(Util.CONSOLE_FAKE_UUID);
        } if (source instanceof User) {
            return this.getDisplayName(((User) source).getUniqueId());
        } else if (source instanceof ServerPlayer) {
            return this.getDisplayName(((ServerPlayer) source).getUniqueId());
        } else if (source instanceof Nameable) {
            return TextComponent.of(((Nameable) source).getName());
        }

        return TextComponent.of("Unknown");
    }

    @Override
    public TextComponent getName(final Audience user) {
        if (user instanceof Nameable) {
            return this.getName((Nameable) user);
        } else if (user instanceof SystemSubject || user instanceof Server) {
            return this.getDisplayName(Util.CONSOLE_FAKE_UUID);
        }
        return TextComponent.of("Unknown");
    }

    @Override
    public TextComponent getName(final Nameable user) {
        final String name = user.getName();
        if (user instanceof User || user instanceof ServerPlayer) {
            return this.addHover(TextComponent.of(name), user.getName());
        }

        return TextComponent.of(name);
    }

    public TextComponent addHover(final TextComponent text, final String user) {
        return this.addCommandToNameInternal(text.toBuilder(), user);
    }

    @Override
    public TextComponent addCommandToName(final Nameable p) {
        final TextComponent.Builder text = TextComponent.builder(p.getName());
        if (p instanceof User || p instanceof ServerPlayer) {
            return this.addCommandToNameInternal(text, p.getName());
        }

        return text.build();
    }

    @Override
    public TextComponent addCommandToDisplayName(final Nameable p) {
        final TextComponent.Builder name = this.getName(p).toBuilder();
        if (p instanceof User || p instanceof ServerPlayer) {
            return this.addCommandToNameInternal(name, p.getName());
        }

        return name.build();
    }

    @Override
    public TextComponent getName(final Object cs) {
        if (cs instanceof Nameable) {
            return this.getName((Nameable) cs);
        } else if (cs instanceof Audience) {
            return this.getName((Audience) cs);
        }
        return TextComponent.empty();
    }

    private TextComponent addCommandToNameInternal(final TextComponent.Builder name, final String user) {
        if (this.commandNameOnClick == null) {
            return name.hoverEvent(HoverEvent.showText(this.messageProviderService.getMessage("name.hover.ign", user))).build();
        }

        final String commandToRun = this.commandNameOnClick.replace("{{subject}}", user).replace("{{player}}", user);
        final TextComponent.Builder hoverAction =
                TextComponent.builder()
                    .append(this.messageProviderService.getMessage("name.hover.ign", user))
                    .append(TextComponent.newline())
                    .append(this.messageProviderService.getMessage("name.hover.command", commandToRun));
        return name.clickEvent(ClickEvent.suggestCommand(commandToRun)).hoverEvent(HoverEvent.showText(hoverAction.build())).build();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.commandNameOnClick = serviceCollection.moduleDataProvider().getModuleConfig(CoreConfig.class).getCommandOnNameClick();
        if (this.commandNameOnClick == null || this.commandNameOnClick.isEmpty()) {
            return;
        }

        if (!this.commandNameOnClick.startsWith("/")) {
            this.commandNameOnClick = "/" + this.commandNameOnClick;
        }

        if (!this.commandNameOnClick.endsWith(" ")) {
            this.commandNameOnClick = this.commandNameOnClick + " ";
        }
    }

}
