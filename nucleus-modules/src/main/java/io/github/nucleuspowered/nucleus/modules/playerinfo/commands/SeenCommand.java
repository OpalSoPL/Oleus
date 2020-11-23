/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.commands;

import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.core.core.CoreKeys;
import io.github.nucleuspowered.nucleus.modules.misc.commands.SpeedCommand;
import io.github.nucleuspowered.nucleus.modules.playerinfo.PlayerInfoPermissions;
import io.github.nucleuspowered.nucleus.modules.playerinfo.services.SeenHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPlayerInformationService;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IPlayerOnlineService;
import io.github.nucleuspowered.nucleus.core.util.functional.TriFunction;
import io.vavr.control.Either;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@EssentialsEquivalent("seen")
@Command(
        aliases = {"seen", "seenplayer", "lookup"},
        basePermission = PlayerInfoPermissions.BASE_SEEN,
        commandDescriptionKey = "seen",
        associatedPermissions = {
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_UUID,
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_IP,
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_FIRSTPLAYED,
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_LASTPLAYED,
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_SPEED_WALKING,
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_SPEED_FLYING,
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_LOCATION,
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_CAN_FLY,
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_IS_FLYING,
            PlayerInfoPermissions.SEEN_EXTENDEDPERMS_GAMEMODE
        }
)
public class SeenCommand implements ICommandExecutor {

    private static final NumberFormat NUMBER_FORMATTER = new DecimalFormat("0.00");

    // keeps order!
    private final ImmutableMap<String, TriFunction<ICommandContext, User, IUserDataObject, Component>> entries
            = ImmutableMap.<String, TriFunction<ICommandContext, User, IUserDataObject, Component>>builder()
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_UUID, this::getUUID)
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_IP, this::getIP)
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_FIRSTPLAYED, this::getFirstPlayed)
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_LASTPLAYED, this::getLastPlayed)
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_SPEED_WALKING, this::getWalkingSpeed)
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_SPEED_FLYING, this::getFlyingSpeed)
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_LOCATION, this::getLocation)
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_CAN_FLY, this::getCanFly)
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_IS_FLYING, this::getIsFlying)
                    .put(PlayerInfoPermissions.SEEN_EXTENDEDPERMS_GAMEMODE, this::getGameMode)
                    .build();

    @Nullable
    private Component getUUID(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return context.getMessage("command.seen.uuid", user.getUniqueId());
    }

    @Nullable
    private Component getIP(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        @Nullable final Tuple<Component, String> res = user.getPlayer()
                    .map(pl -> Tuple.of(
                            context.getMessage("command.seen.ipaddress",
                                    pl.getConnection().getAddress().getAddress().toString()),
                                    pl.getConnection().getAddress().getAddress().toString()))
                    .orElseGet(() -> userDataModule.get(CoreKeys.IP_ADDRESS).map(x ->
                            Tuple.of(
                                    context.getMessage("command.seen.lastipaddress", x),
                                    x
                            )).orElse(null));
        if (res != null) {
            if (Sponge.getCommandManager().getCommandMapping("nucleus:getfromip").isPresent()) {
                return res.getFirst()
                        .hoverEvent(HoverEvent.showText(context.getMessage("command.seen.ipclick")))
                        .clickEvent(ClickEvent.runCommand("/nucleus:getfromip " + res.getSecond().replaceAll("^/", "")));
            }
            return res.getFirst();
        }

        return null;
    }

    @Nullable
    private Component getFirstPlayed(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        Optional<Instant> i = user.get(Keys.FIRST_DATE_JOINED);
        return i.map(x -> context.getMessage("command.seen.firstplayed",
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withLocale(context.getLocale())
                        .withZone(ZoneId.systemDefault()).format(x))).orElse(null);
    }

    @Nullable
    private Component getLastPlayed(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        if (user.isOnline()) {
            return null;
        }

        return user.get(Keys.LAST_DATE_PLAYED).map(x -> context.getMessage("command.seen.lastplayed",
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withLocale(context.getLocale())
                        .withZone(ZoneId.systemDefault()).format(x))).orElse(null);
    }

    @Nullable
    private Component getLocation(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        if (user.isOnline()) {
            return this.getLocationString("command.seen.currentlocation", user.getPlayer().get().getServerLocation(), context);
        }

        final Optional<WorldProperties> wp = Sponge.getServer().getWorldManager().getProperties(user.getWorldKey());
        return wp.map(worldProperties ->
                this.getLocationString("command.seen.lastlocation", worldProperties.getKey(), user.getPosition(), context))
                .orElseGet(() -> context.getMessage("standard.unknown"));
    }

    @Nullable
    private Component getWalkingSpeed(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return user.get(Keys.WALKING_SPEED)
                .map(x -> context.getMessage("command.seen.speed.walk", NUMBER_FORMATTER.format(x * SpeedCommand.multiplier)))
                .orElse(null);
    }

    @Nullable
    private Component getFlyingSpeed(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return user.get(Keys.FLYING_SPEED)
                .map(x -> context.getMessage("command.seen.speed.fly", NUMBER_FORMATTER.format(x * SpeedCommand.multiplier)))
                .orElse(null);
    }

    @Nullable
    private Component getCanFly(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return context.getMessage("command.seen.canfly", this.getYesNo(user.get(Keys.CAN_FLY).orElse(false), context));
    }

    @Nullable
    private Component getIsFlying(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return context.getMessage("command.seen.isflying", this.getYesNo(user.get(Keys.IS_FLYING).orElse(false), context));
    }

    @Nullable
    private Component getGameMode(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return user.get(Keys.GAME_MODE).map(x -> context.getMessage("command.seen.gamemode", x.getKey().asString())).orElse(null);
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            NucleusParameters.Composite.USER_OR_GAME_PROFILE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Either<User, GameProfile> target = NucleusParameters.Composite.parseUserOrGameProfile(context);
        final User user = target.fold(Function.identity(), x -> Sponge.getServer().getUserManager().getOrCreate(x));
        final IUserDataObject userDataObject = context.getServiceCollection()
                .storageManager()
                .getUserService()
                .getOrNewOnThread(user.getUniqueId());

        final List<Component> messages = new ArrayList<>();

        // Everyone gets the last online time.
        final IPlayerOnlineService playerOnlineService = context.getServiceCollection().playerOnlineService();
        if (context.getAsPlayer().map(x -> playerOnlineService.isOnline(x, user)).orElse(true)) {
            messages.add(context.getMessage("command.seen.iscurrently.online", user.getName()));
            userDataObject.get(CoreKeys.LAST_LOGIN).ifPresent(x -> messages.add(
                    context.getMessage("command.seen.loggedon", context.getTimeString(Duration.between(x, Instant.now())))));
        } else {
            messages.add(context.getMessage("command.seen.iscurrently.offline", user.getName()));
            playerOnlineService.lastSeen(context.getAsPlayer().orElse(null), user).ifPresent(x -> messages.add(
                    context.getMessage("command.seen.loggedoff", context.getTimeString(Duration.between(Instant.now(), x)))));
        }

        messages.add(context.getMessage("command.seen.displayname", context.getDisplayName(user.getUniqueId())));

        messages.add(Component.empty());
        for (final Map.Entry<String, TriFunction<ICommandContext, User, IUserDataObject, Component>> entry : this.entries.entrySet()) {
            if (context.testPermission(entry.getKey())) {
                @Nullable final Component m = entry.getValue().accept(context, user, userDataObject);
                if (m != null) {
                    messages.add(m);
                }
            }
        }

        // Add the extra module information.
        // TODO: Ordering
        final IPlayerInformationService playerInformationService = context.getServiceCollection().playerInformationService();
        for (final IPlayerInformationService.Provider provider : playerInformationService.getProviders()) {
            provider.get(user, context.getCause(), context.getServiceCollection()).ifPresent(messages::add);
        }

        messages.addAll(context.getServiceCollection().getServiceUnchecked(SeenHandler.class)
                .getText(context.getCause(), user));

        Util.getPaginationBuilder(context.getAudience())
                .contents(messages)
                .padding(Component.text("-", NamedTextColor.GREEN))
                .title(context.getMessage("command.seen.title", user.getName())).sendTo(context.getAudience());
        return context.successResult();
    }

    private Component getLocationString(final String key, final ServerLocation lw, final ICommandContext source) {
        return this.getLocationString(key, lw.getWorldKey(), lw.getPosition(), source);
    }

    private Component getLocationString(final String key, final ResourceKey worldKey, final Vector3d position, final ICommandContext context) {
        final Component text = context.getMessage(key, context.getMessage("command.seen.locationtemplate", worldKey.asString(),
                position.toInt().toString()));
        if (Sponge.getCommandManager().getCommandMapping("nucleus:tppos")
                .map(x -> x.getRegistrar().canExecute(context.getCause(), x))
                .orElse(false)) {

            final TextComponent.Builder building = Component.text().append(text)
                    .hoverEvent(HoverEvent.showText(context.getMessage("command.seen.teleportposition")
            ));

            Sponge.getServer().getWorldManager().getWorld(worldKey).ifPresent(
                    x -> building.clickEvent(SpongeComponents.executeCallback(cs -> {
                        if (cs.root() instanceof ServerPlayer) {
                            ((ServerPlayer) cs.root()).setLocation(ServerLocation.of(x, position));
                        }
                    })
            ));

            return building.build();
        }

        return text;
    }

    private String getYesNo(@Nullable Boolean bool, final ICommandContext context) {
        if (bool == null) {
            bool = false;
        }

        return context.getMessageString("standard.yesno." + bool.toString().toLowerCase());
    }
}
