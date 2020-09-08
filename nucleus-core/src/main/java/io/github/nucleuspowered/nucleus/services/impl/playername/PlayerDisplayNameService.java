/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.playername;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.Constants;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.services.interfaces.ITextStyleService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Nameable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Singleton
public class PlayerDisplayNameService implements IPlayerDisplayNameService, IReloadableService.Reloadable {

    private final LinkedHashSet<DisplayNameResolver> resolvers = new LinkedHashSet<>();
    private final LinkedHashSet<DisplayNameQuery> queries = new LinkedHashSet<>();

    private final IMessageProviderService messageProviderService;
    private final IPermissionService permissionService;
    private final ITextStyleService textStyleService;

    private String commandNameOnClick = null;

    @Inject
    public PlayerDisplayNameService(final INucleusServiceCollection serviceCollection) {
        this.messageProviderService = serviceCollection.messageProvider();
        this.permissionService = serviceCollection.permissionService();
        this.textStyleService = serviceCollection.textStyleService();
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
        final TextComponent.Builder builder;
        if (playerUUID == Util.CONSOLE_FAKE_UUID) {
            return this.getName(Sponge.getSystemSubject());
        }
       final User user = Sponge.getServer()
                .getUserManager()
                .get(playerUUID)
                .orElseThrow(() -> new IllegalArgumentException("UUID does not map to a player"));
        TextComponent userName = null;
        for (final DisplayNameResolver resolver : this.resolvers) {
            final Optional<TextComponent> optionalUserName = resolver.resolve(playerUUID);
            if (optionalUserName.isPresent()) {
                userName = optionalUserName.get();
                break;
            }
        }

        if (userName == null) {
            builder = TextComponent.builder(user.getName());
        } else {
            builder = TextComponent.builder().append(userName);
        }

        // Set name colours
        this.addCommandToNameInternal(builder, user.getName());
        this.applyStyle(user, builder);
        return builder.build();
    }

    private void applyStyle(final Subject subject, final TextComponent.Builder builder) {
        builder.color(this.getColour(subject).orElse(null)).style(this.getStyle(subject));
    }

    private Optional<TextColor> getColour(final Subject player) {
        final Optional<TextColor> os = this.permissionService.getOptionFromSubject(player, "namecolour", "namecolor")
                .flatMap(this.textStyleService::getColourFromString);
        // TODO: Get from chat config - need a pluggable system
        return os;
    }

    private Style getStyle(final Subject player) {
        final Optional<Style> style = this.permissionService.getOptionFromSubject(player, "namecolour", "namecolor")
                .map(this.textStyleService::getTextStyleFromString);
        // TODO: Get from chat config - need a pluggable system
        return style.orElseGet(Style::empty);
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
        final TextComponent.Builder builder = TextComponent.builder(name);
        if (user instanceof User || user instanceof ServerPlayer) {
            this.addCommandToNameInternal(builder, name);
        }


        return TextComponent.of(name);
    }

    @Override
    public TextComponent addCommandToName(final Nameable p) {
        final TextComponent.Builder text = TextComponent.builder(p.getName());
        if (p instanceof User || p instanceof ServerPlayer) {
            this.addCommandToNameInternal(text, p.getName());
        }

        return text.build();
    }

    @Override
    public TextComponent addCommandToDisplayName(final Nameable p) {
        final TextComponent.Builder name = this.getName(p).toBuilder();
        if (p instanceof User || p instanceof ServerPlayer) {
            this.addCommandToNameInternal(name, p.getName());
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

    private void addCommandToNameInternal(final TextComponent.Builder name, final String user) {
        if (this.commandNameOnClick == null) {
            name.hoverEvent(HoverEvent.showText(this.messageProviderService.getMessage("name.hover.ign", user))).build();
            return;
        }

        final String commandToRun = this.commandNameOnClick.replace("{{subject}}", user).replace("{{player}}", user);
        final TextComponent.Builder hoverAction =
                TextComponent.builder()
                    .append(this.messageProviderService.getMessage("name.hover.ign", user))
                    .append(TextComponent.newline())
                    .append(this.messageProviderService.getMessage("name.hover.command", commandToRun));
        name.clickEvent(ClickEvent.suggestCommand(commandToRun)).hoverEvent(HoverEvent.showText(hoverAction.build())).build();
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.commandNameOnClick = serviceCollection.configProvider().getModuleConfig(CoreConfig.class).getCommandOnNameClick();
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
