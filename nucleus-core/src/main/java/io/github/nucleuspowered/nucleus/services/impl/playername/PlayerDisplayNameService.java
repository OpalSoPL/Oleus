/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.playername;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

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
        final Optional<User> withRealName = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(displayName);
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

    @Override public Map<UUID, List<String>> startsWith(final String displayName) {
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
    public Optional<User> getUser(final Text displayName) {
        return getUser(displayName.toPlain());
    }

    @Override
    public Text getDisplayName(final UUID playerUUID) {
        if (playerUUID == Util.CONSOLE_FAKE_UUID) {
            return getDisplayName(Sponge.getServer().getConsole());
        }

        final User user = Sponge.getServiceManager()
                .provideUnchecked(UserStorageService.class)
                .get(playerUUID)
                .orElseThrow(() -> new IllegalArgumentException("UUID does not map to a player"));
        for (final DisplayNameResolver resolver : this.resolvers) {
            final Optional<Text> userName = resolver.resolve(playerUUID);
            if (userName.isPresent()) {
                return userName
                        .map(x -> this.addHover(x, user))
                        .get();
            }
        }

        // Set name colours

        return this.addHover(user.get(Keys.DISPLAY_NAME).orElseGet(() -> Text.of(user.getName())), user);
    }

    @Override
    public Text getDisplayName(final CommandSource source) {
        if (source instanceof User) {
            return this.getDisplayName(((User) source).getUniqueId());
        }

        return Text.of(source.getName());
    }

    @Override
    public Text getName(final CommandSource user) {
        if (user instanceof User) {
            return this.addHover(Text.of(user.getName()), (User) user, null, null);
        }

        return Text.of(user.getName());
    }

    public Text addHover(final Text text, final User user) {
        return this.addHover(text, user, null, null);
    }

    private Text addHover(final Text text, final User user, @Nullable final TextColor colour, @Nullable final TextStyle style) {
        final Text.Builder builder = text.toBuilder();
        if (colour != null) {
            builder.color(colour);
        }
        if (style != null) {
            builder.style(style);
        }
        return this.addCommandToNameInternal(builder, user);
    }

    @Override public Text addCommandToName(final CommandSource p) {
        final Text.Builder text = Text.builder(p.getName());
        if (p instanceof User) {
            return this.addCommandToNameInternal(text, (User)p);
        }

        return text.build();
    }

    @Override public Text addCommandToDisplayName(final CommandSource p) {
        final Text.Builder name = this.getName(p).toBuilder();
        if (p instanceof User) {
            return this.addCommandToNameInternal(name, (User)p);
        }

        return name.build();
    }

    private Text addCommandToNameInternal(final Text.Builder name, final User user) {
        if (this.commandNameOnClick == null) {
            return name.onHover(TextActions.showText(this.messageProviderService.getMessage("name.hover.ign", user.getName()))).build();
        }

        final String commandToRun = this.commandNameOnClick.replace("{{subject}}", user.getName()).replace("{{player}}", user.getName());
        final Text.Builder hoverAction =
                Text.builder()
                    .append(this.messageProviderService.getMessage("name.hover.ign", user.getName()))
                    .append(Text.NEW_LINE)
                    .append(this.messageProviderService.getMessage("name.hover.command", commandToRun));
        return name.onClick(TextActions.suggestCommand(commandToRun)).onHover(TextActions.showText(hoverAction.toText())).build();
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

    /*
    private <T extends TextElement> T getStyle(User player,
            Function<String, T> returnIfAvailable,
            Function<ChatTemplateConfig, T> fromTemplate,
            T def,
            String... options) {
        Optional<String> os = this.permissionService.getOptionFromSubject(player, options);
        if (os.isPresent()) {
            return returnIfAvailable.apply(os.get());
        }

        return getService(ChatService.class).map(templateUtil -> fromTemplate.apply(templateUtil.getTemplateNow(player))).orElse(def);

    }
     */

}
