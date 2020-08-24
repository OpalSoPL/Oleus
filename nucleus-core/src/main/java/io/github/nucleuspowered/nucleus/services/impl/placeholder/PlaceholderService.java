/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.placeholder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.core.services.UniqueUserService;
import io.github.nucleuspowered.nucleus.services.IInitService;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.placeholder.standard.NamePlaceholder;
import io.github.nucleuspowered.nucleus.services.impl.placeholder.standard.NamedOptionPlaceholder;
import io.github.nucleuspowered.nucleus.services.impl.placeholder.standard.OptionPlaceholder;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlaceholderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class PlaceholderService implements IPlaceholderService, IInitService {

    private static final Pattern SUFFIX_PATTERN = Pattern.compile(":([sp]+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SEPARATOR;
    private final PlaceholderParser optionParser;
    private final PlaceholderParser emptyParser;
    private final Map<String, PlaceholderMetadata> parsers = new HashMap<>();
    private final PluginContainer pluginContainer;

    static {
        SEPARATOR = buildModifiers();
    }

    private static Pattern buildModifiers() {
        final StringBuilder builder = new StringBuilder(":([");
        for (final TextModifiers m : TextModifiers.values()) {
            builder.append(m.getKey());
        }
        builder.append("]+)$");
        return Pattern.compile(builder.toString());
    }

    @Inject
    public PlaceholderService(final INucleusServiceCollection serviceCollection) {
        this.pluginContainer = serviceCollection.pluginContainer();
        this.optionParser = new OptionPlaceholder(serviceCollection.permissionService());
        this.emptyParser = PlaceholderParser.builder().plugin(this.pluginContainer).id("empty").parser(p -> Text.EMPTY).name("Empty parser").build();
    }

    @Override
    public void init(final INucleusServiceCollection serviceCollection) {
        // player, variables, map?
        this.registerToken("empty", this.emptyParser, false);
        final NamePlaceholder normalName = new NamePlaceholder(
                serviceCollection.playerDisplayNameService(),
                IPlayerDisplayNameService::addCommandToName,
                "nucleus:name",
                "Nucleus Name placeholder");
        this.registerToken("name", normalName);
        this.registerToken("playername", normalName);
        this.registerToken("subject", new NamePlaceholder(
                serviceCollection.playerDisplayNameService(),
                IPlayerDisplayNameService::addCommandToName,
                "nucleus:subject",
                "Nucleus subject (including console) placeholder",
                true));

        final NamePlaceholder displayName = new NamePlaceholder(
                serviceCollection.playerDisplayNameService(),
                IPlayerDisplayNameService::getDisplayName,
                "nucleus:displayname",
                "Nucleus subject (including console) placeholder");
        this.registerToken("player", displayName);
        this.registerToken("playerdisplayname", displayName);
        this.registerToken("displayname", displayName);

        final IPermissionService permissionService = serviceCollection.permissionService();
        this.registerToken("option", this.optionParser);
        this.registerToken("prefix", new NamedOptionPlaceholder(permissionService, "prefix"));
        this.registerToken("suffix", new NamedOptionPlaceholder(permissionService, "suffix"));

        this.registerToken("maxplayers", PlaceholderParser.builder()
                .plugin(this.pluginContainer)
                .id("maxplayers")
                .name("Nucleus Max Players parser")
                .parser(p -> Text.of(Sponge.getServer().getMaxPlayers()))
                .build());
        this.registerToken("onlineplayers", PlaceholderParser.builder()
                        .plugin(this.pluginContainer)
                        .id("onlineplayers")
                        .name("Nucleus Online Player Count parser")
                        .parser(p -> Text.of(Sponge.getServer().getOnlinePlayers().size()))
                        .build());
        this.registerToken("currentworld", PlaceholderParser.builder()
                .plugin(this.pluginContainer)
                .id("currentworld")
                .name("Nucleus Current World parser")
                .parser(placeholder -> Text.of(getWorld(placeholder)))
                .build());
        this.registerToken("time",
                PlaceholderParser.builder()
                        .plugin(this.pluginContainer)
                        .id("time")
                        .name("Nucleus world time parser")
                        .parser(placeholder ->
                                Text.of(Util.getTimeFromTicks(serviceCollection.messageProvider(), getWorld(placeholder).getProperties().getWorldTime())))
                        .build());

        this.registerToken("uniquevisitor",
                PlaceholderParser.builder()
                        .plugin(this.pluginContainer)
                        .id("uniquevisitor")
                        .name("Nucleus unique visitor parser")
                        .parser(placeholder -> Text.of(serviceCollection.getServiceUnchecked(UniqueUserService.class).getUniqueUserCount()))
                        .build());
        this.registerToken("ipaddress",
                PlaceholderParser.builder()
                        .plugin(this.pluginContainer)
                        .id("ipaddress")
                        .name("Nucleus IP Address parser")
                        .parser(placeholder -> placeholder.getAssociatedObject().filter(x -> x instanceof RemoteSource)
                                .map(x -> Text.of(((RemoteSource) x).getConnection().getAddress().getAddress().toString()))
                                .orElse(Text.of("localhost")))
                        .build());
    }

    @Override
    public Set<PlaceholderParser> getParsers() {
        return this.parsers.values().stream().map(PlaceholderMetadata::getParser).collect(Collectors.toSet());
    }

    @Override
    public TextRepresentable parse(@Nullable final CommandSource commandSource, final String input) {
        String token = input.toLowerCase().trim().replace("{{", "").replace("}}", "");
        final Matcher m = SUFFIX_PATTERN.matcher(token);
        final List<Function<Text, Text>> modifiersCollection;
        if (m.find(0)) {
            final String match = m.group(1).toLowerCase();
            modifiersCollection = new ArrayList<>();
            for (final TextModifiers modifier : TextModifiers.values()) {
                if (match.contains(modifier.getKey())) {
                    modifiersCollection.add(modifier);
                }
            }

            token = token.replaceAll(SUFFIX_PATTERN.pattern(), "");
        } else {
            modifiersCollection = ImmutableList.of();
        }

        final PlaceholderParser parser;
        final PlaceholderContext context;
        if (token.startsWith("o:")) {
            if (commandSource == null) {
                return Text.EMPTY;
            }
            // option
            parser = this.optionParser;
            context = this.contextForSubjectAndOption(commandSource, token.substring(2));
        } else {
            final String[] s = token.split("\\|", 2);
            final String tokenIn = s[0].toLowerCase();
            final String arg = s.length == 2 ? s[1] : null;
            context = PlaceholderContext.builder()
                    .setAssociatedObject(commandSource)
                    .setArgumentString(arg)
                    .build();
            parser = this.getParser(tokenIn).orElse(this.emptyParser);
        }

        return new NucleusPlaceholderText(context, parser, modifiersCollection);
    }

    @Override
    public void registerToken(final String tokenName, final PlaceholderParser parser) {
        this.registerToken(tokenName, parser, true);
    }

    @Override
    public void registerToken(final String tokenName, final PlaceholderParser parser, final boolean document) {
        if (SEPARATOR.asPredicate().test(tokenName)) {
            // can't be registered.
            throw new IllegalArgumentException("Tokens must not contain |, :, _ or space characters.");
        }
        final String token = tokenName.toLowerCase();
        if (!this.parsers.containsKey(token)) {
            this.parsers.put(token, new PlaceholderMetadata(token, parser, document));
        } else {
            throw new IllegalStateException("Token " + tokenName.toLowerCase() + " has already been registered.");
        }
    }

    @Override
    public Optional<PlaceholderParser> getParser(final String token) {
        if (token.contains(":")) {
            return Sponge.getRegistry().getType(PlaceholderParser.class, token);
        }
        final PlaceholderMetadata placeholderMetadata = this.parsers.get(SEPARATOR.split(token.toLowerCase(), 2)[0]);
        if (placeholderMetadata == null) {
            return Optional.empty();
        }
        return Optional.of(placeholderMetadata.getParser());
    }

    @Override
    public PlaceholderParser optionParser() {
        return this.optionParser;
    }

    private PlaceholderContext contextForSubjectAndOption(final Subject subject, final String option) {
        return PlaceholderContext.builder().setArgumentString(option).setAssociatedObject(subject).build();
    }

    @Override
    public PlaceholderTextComponent textForSubjectAndOption(final Subject subject, final String option) {
        return PlaceholderText.builder()
                .setParser(this.optionParser)
                .setContext(this.contextForSubjectAndOption(subject, option))
                .build();
    }

    @Override public Map<String, PlaceholderMetadata> getNucleusParsers() {
        return ImmutableMap.copyOf(this.parsers);
    }

    // --

    private static ServerWorld getWorld(final PlaceholderContext placeholder) {
        final CommandSource p = placeholder.getAssociatedObject()
                .filter(x -> x instanceof Locatable).map(x -> (Locatable) x)
                .orElseGet(Sponge.getServer()::getConsole);
        final ServerWorld world;
        if (p instanceof Locatable) {
            world = ((Locatable) p).getWorld();
        } else {
            world = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
        }

        return world;
    }

}
