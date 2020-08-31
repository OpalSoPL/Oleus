/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.services;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.api.module.nickname.NucleusNicknameService;
import io.github.nucleuspowered.nucleus.api.module.nickname.exception.NicknameException;
import io.github.nucleuspowered.nucleus.modules.nickname.NicknameKeys;
import io.github.nucleuspowered.nucleus.modules.nickname.NicknamePermissions;
import io.github.nucleuspowered.nucleus.modules.nickname.config.NicknameConfig;
import io.github.nucleuspowered.nucleus.modules.nickname.events.ChangeNicknameEventPost;
import io.github.nucleuspowered.nucleus.modules.nickname.events.ChangeNicknameEventPre;
import io.github.nucleuspowered.nucleus.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.scaffold.service.annotations.APIService;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.services.interfaces.IStorageManager;
import io.github.nucleuspowered.nucleus.services.interfaces.ITextStyleService;
import io.github.nucleuspowered.nucleus.util.CauseStackHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import com.google.inject.Inject;

@APIService(NucleusNicknameService.class)
public class NicknameService implements NucleusNicknameService, IReloadableService.Reloadable, ServiceBase {

    private final IMessageProviderService messageProviderService;
    private final IStorageManager storageManager;
    private final ITextStyleService textStyleService;

    @Inject
    public NicknameService(final INucleusServiceCollection serviceCollection) {
        this.messageProviderService = serviceCollection.messageProvider();
        this.storageManager = serviceCollection.storageManager();
        this.textStyleService = serviceCollection.textStyleService();
    }

    private TextComponent prefix = Text.EMPTY;
    private Pattern pattern;
    private int min = 3;
    private int max = 16;
    private final List<UUID> cached = Lists.newArrayList();
    private final BiMap<UUID, String> cache = HashBiMap.create();
    private final BiMap<UUID, Text> textCache = HashBiMap.create();
    private final TreeMap<String, UUID> reverseLowerCaseCache = new TreeMap<>();

    public void injectResolver(final INucleusServiceCollection serviceCollection) {
        serviceCollection.playerDisplayNameService().provideDisplayNameResolver(this::getNicknameWithPrefix);
        serviceCollection.playerDisplayNameService().provideDisplayNameQuery(
                new IPlayerDisplayNameService.DisplayNameQuery() {
                    @Override public Optional<User> resolve(final String name) {
                        return getFromCache(name).map(x -> x);
                    }

                    @Override public Map<UUID, String> startsWith(final String name) {
                        return startsWithUUIDStringMap(name);
                    }
                }
        );
    }

    public void markRead(final UUID player) {
        this.cached.add(player);
    }

    public void updateCache(final UUID player, final TextComponent text) {
        this.cache.put(player, text.toPlain());
        this.textCache.put(player, text);
    }

    public Optional<Player> getFromCache(final String text) {
        final UUID u = this.cache.inverse().get(text);
        if (u != null) {
            final Optional<Player> ret = Sponge.getServer().getPlayer(u);
            if (!ret.isPresent()) {
                this.cache.remove(u);
            }

            return ret;
        }

        return Optional.empty();
    }

    public Map<String, UUID> getAllCached() {
        return Maps.newHashMap(this.cache.inverse());
    }

    public Map<Player, Text> getFromSubstring(final String search) {
        final String prefix = search.toLowerCase();
        final Collection<UUID> uuidCollection;
        if (prefix.length() > 0) {
            final char nextLetter = (char) (prefix.charAt(prefix.length() -1) + 1);
            final String end = prefix.substring(0, prefix.length()-1) + nextLetter;
            uuidCollection = this.reverseLowerCaseCache.subMap(prefix, end).values();
        } else {
            uuidCollection = this.reverseLowerCaseCache.values();
        }

        final ImmutableMap.Builder<Player, Text> mapToReturn = ImmutableMap.builder();
        Sponge.getServer().getOnlinePlayers().stream()
                .filter(x -> !this.cache.containsKey(x.getUniqueId()))
                .filter(x -> x.getName().toLowerCase().startsWith(prefix))
                .forEach(player -> mapToReturn.put(player, player.get(Keys.DISPLAY_NAME).orElseGet(
                        () -> Text.of(player.getName(), "*"))));

        for (final UUID uuid : uuidCollection) {
            final Optional<Player> op = Sponge.getServer().getPlayer(uuid);
            op.ifPresent(player -> mapToReturn.put(player, this.textCache.get(uuid)));
        }

        return mapToReturn.build();
    }

    public Map<String, UUID> startsWithGetMap(final String text) {
        return this.cache.inverse().entrySet().stream().filter(x -> x.getKey().startsWith(text.toLowerCase()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<UUID, String> startsWithUUIDStringMap(final String text) {
        return this.cache.inverse().entrySet().stream().filter(x -> x.getKey().startsWith(text.toLowerCase()))
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }


    public List<UUID> startsWith(final String text) {
        return this.cache.inverse().entrySet().stream().filter(x -> x.getKey().startsWith(text.toLowerCase()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public void removeFromCache(final UUID player) {
        this.cache.remove(player);
        this.textCache.remove(player);
        this.cached.remove(player);
    }

    @Override
    public Optional<Text> getNicknameWithPrefix(final UUID user) {
        return getNickname(user).map(x -> Text.join(this.prefix, x));
    }

    @Override
    public Optional<Text> getNicknameWithPrefix(final User user) {
        return getNickname(user).map(x -> Text.join(this.prefix, x));
    }

    @Override
    public Optional<Text> getNickname(final User user) {
        return getNickname(user.getUniqueId());
    }

    @Override
    public Optional<Text> getNickname(final UUID user) {
        if (this.cached.contains(user)) {
            return Optional.ofNullable(this.textCache.get(user));
        }
        return this.storageManager.getUserService()
                .getOnThread(user)
                .flatMap(x -> x.get(NicknameKeys.USER_NICKNAME_JSON))
                .map(TextSerializers.JSON::deserialize);
    }

    @Override
    public void setNickname(final User user, @Nullable final TextComponent nickname, final boolean bypassRestrictions) throws NicknameException {
        final Cause cause = Sponge.getCauseStackManager().getCurrentCause();
        if (nickname != null) {
            setNick(user, cause, nickname, bypassRestrictions);
        } else {
            removeNick(user, cause);
        }

    }

    public void removeNick(final User user, final CommandSource src) throws NicknameException {
        removeNick(user, CauseStackHelper.createCause(src));
    }

    private void removeNick(User user, final Cause cause) throws NicknameException {
        final TextComponent currentNickname = getNickname(user).orElse(null);
        if (!(user instanceof Player) && user.getPlayer().isPresent()) {
            user = user.getPlayer().get();
        }

        final ChangeNicknameEventPre cne = new ChangeNicknameEventPre(cause, currentNickname, null, user);
        if (Sponge.getEventManager().post(cne)) {
            throw new NicknameException(
                    this.messageProviderService.getMessage("command.nick.eventcancel", user.getName()),
                    NicknameException.Type.EVENT_CANCELLED
            );
        }

        final IUserDataObject mus = this.storageManager.getUserService()
                .getOnThread(user.getUniqueId())
                .orElseThrow(() -> new NicknameException(
                        this.messageProviderService.getMessage("standard.error.nouser"),
                        NicknameException.Type.NO_USER
                ));

        mus.remove(NicknameKeys.USER_NICKNAME_JSON);
        removeFromCache(user.getUniqueId());
        Sponge.getEventManager().post(new ChangeNicknameEventPost(cause, currentNickname, null, user));

        if (user.isOnline()) {
            user.getPlayer().ifPresent(x ->
                    this.messageProviderService.sendMessageTo(x, "command.delnick.success.base"));
        }
    }

    public void setNick(final User pl, final CommandSource src, final TextComponent nickname, final boolean bypass) throws NicknameException {
        setNick(pl, CauseStackHelper.createCause(src), nickname, bypass);
    }

    private void setNick(User pl, final Cause cause, final TextComponent nickname, final boolean bypass) throws NicknameException {
        final String plain = nickname.toPlain().trim();
        if (plain.isEmpty()) {
            throw new NicknameException(
                    this.messageProviderService.getMessage("command.nick.tooshort"),
                    NicknameException.Type.TOO_SHORT
            );
        }

        // Does the user exist?
        try {
            final Optional<User> match =
                    Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(nickname.toPlain().trim());

            // The only person who can use such a name is oneself.
            if (match.isPresent() && !match.get().getUniqueId().equals(pl.getUniqueId())) {
                // Fail - cannot use another's name.
                throw new NicknameException(
                        this.messageProviderService.getMessage("command.nick.nameinuse", plain),
                        NicknameException.Type.NOT_OWN_IGN);
            }
        } catch (final IllegalArgumentException ignored) {
            // We allow some other nicknames too.
        }

        if (!(pl instanceof Player) && pl.getPlayer().isPresent()) {
            pl = pl.getPlayer().get();
        }

        if (!bypass) {
            // Giving subject must have the colour permissions and whatnot. Also,
            // colour and color are the two spellings we support. (RULE BRITANNIA!)
            final Optional<Subject> os = cause.first(Subject.class);
            if (os.isPresent()) {
                stripPermissionless(os.get(), nickname);
            }

            if (!this.pattern.matcher(plain).matches()) {
                throw new NicknameException(
                        this.messageProviderService.getMessage("command.nick.nopattern", this.pattern.pattern()),
                        NicknameException.Type.INVALID_PATTERN);
            }

            final int strippedNameLength = plain.length();

            // Do a regex remove to check minimum length requirements.
            if (strippedNameLength < Math.max(this.min, 1)) {
                throw new NicknameException(
                        this.messageProviderService.getMessage("command.nick.tooshort"),
                        NicknameException.Type.TOO_SHORT
                );
            }

            // Do a regex remove to check maximum length requirements. Will be at least the minimum length
            if (strippedNameLength > Math.max(this.max, this.min)) {
                throw new NicknameException(
                        this.messageProviderService.getMessage("command.nick.toolong"),
                        NicknameException.Type.TOO_SHORT
                );
            }
        }

        // Send an event
        final TextComponent currentNickname = getNickname(pl).orElse(null);
        final ChangeNicknameEventPre cne = new ChangeNicknameEventPre(cause, currentNickname, nickname, pl);
        if (Sponge.getEventManager().post(cne)) {
            throw new NicknameException(
                    this.messageProviderService.getMessage("command.nick.eventcancel", pl.getName()),
                    NicknameException.Type.EVENT_CANCELLED
            );
        }

        final IUserDataObject userDataObject = this.storageManager.getUserService().getOrNewOnThread(pl.getUniqueId());
        userDataObject.set(NicknameKeys.USER_NICKNAME_JSON, TextSerializers.JSON.serialize(nickname));
        this.updateCache(pl.getUniqueId(), nickname);

        Sponge.getEventManager().post(new ChangeNicknameEventPost(cause, currentNickname, nickname, pl));
        pl.getPlayer().ifPresent(player -> player.sendMessage(Text.builder().append(
                this.messageProviderService.getMessage("command.nick.success.base"))
                    .append(Text.of(" - ", TextColors.RESET, nickname)).build()));

    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        final NicknameConfig nc = serviceCollection.configProvider().getModuleConfig(NicknameConfig.class);
        this.pattern = nc.getPattern();
        this.min = nc.getMinNicknameLength();
        this.max = nc.getMaxNicknameLength();
        this.prefix = TextSerializers.FORMATTING_CODE.deserialize(nc.getPrefix());
    }

    private void stripPermissionless(final Subject source, final TextComponent message) throws NicknameException {
        final Collection<String> strings = this.textStyleService.wouldStrip(
                NicknamePermissions.NICKNAME_COLOUR,
                NicknamePermissions.NICKNAME_STYLE,
                source,
                TextSerializers.FORMATTING_CODE.serialize(message));
        if (!strings.isEmpty()) {
            throw new NicknameException(this.messageProviderService.getMessage("command.nick.nopermscolourstyle", String.join(", ", strings)),
                    NicknameException.Type.INVALID_STYLE_OR_COLOUR);
        }
    }

    public TextComponent getNickPrefix() {
        return this.prefix;
    }
}
