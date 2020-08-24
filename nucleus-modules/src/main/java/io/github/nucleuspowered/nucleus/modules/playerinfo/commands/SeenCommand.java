/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.playerinfo.commands;

import org.spongepowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.core.CoreKeys;
import io.github.nucleuspowered.nucleus.modules.misc.commands.SpeedCommand;
import io.github.nucleuspowered.nucleus.modules.playerinfo.PlayerInfoPermissions;
import io.github.nucleuspowered.nucleus.modules.playerinfo.services.SeenHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerInformationService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerOnlineService;
import io.github.nucleuspowered.nucleus.util.TriFunction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

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

import javax.annotation.Nullable;

@EssentialsEquivalent("seen")
@Command(
        aliases = {"seen", "seenplayer", "lookup"},
        basePermission = PlayerInfoPermissions.BASE_SEEN,
        commandDescriptionKey = "seen",
        async = true,
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
    private final ImmutableMap<String, TriFunction<ICommandContext, User, IUserDataObject, Text>> entries
            = ImmutableMap.<String, TriFunction<ICommandContext, User, IUserDataObject, Text>>builder()
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
    private TextComponent getUUID(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return context.getMessage("command.seen.uuid", user.getUniqueId());
    }

    @Nullable
    private TextComponent getIP(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        @Nullable final Tuple<Text, String> res = user.getPlayer()
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
            if (Sponge.getCommandManager().get("nucleus:getfromip").isPresent()) {
                return res.getFirst().toBuilder()
                        .onHover(TextActions.showText(context.getMessage("command.seen.ipclick")))
                        .onClick(TextActions.runCommand("/nucleus:getfromip " + res.getSecond().replaceAll("^/", "")))
                        .build();
            }
            return res.getFirst();
        }

        return null;
    }

    @Nullable
    private TextComponent getFirstPlayed(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        Optional<Instant> i = user.get(Keys.FIRST_DATE_PLAYED);
        if (!i.isPresent()) {
            i = userDataModule.get(CoreKeys.FIRST_JOIN);
        }

        return i.map(x -> context.getMessage("command.seen.firstplayed",
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withLocale(context.getCommandSourceRoot().getLocale())
                        .withZone(ZoneId.systemDefault()).format(x))).orElse(null);
    }

    @Nullable
    private TextComponent getLastPlayed(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        if (user.isOnline()) {
            return null;
        }

        return user.get(Keys.LAST_DATE_PLAYED).map(x -> context.getMessage("command.seen.lastplayed",
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        .withLocale(context.getCommandSourceRoot().getLocale())
                        .withZone(ZoneId.systemDefault()).format(x))).orElse(null);
    }

    @Nullable
    private TextComponent getLocation(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        if (user.isOnline()) {
            return getLocationString("command.seen.currentlocation", user.getPlayer().get().getLocation(), context);
        }

        final Optional<WorldProperties> wp = user.getWorldUniqueId().flatMap(x -> Sponge.getServer().getWorldProperties(x));
        return wp.map(worldProperties ->
                    getLocationString("command.seen.lastlocation", worldProperties, user.getPosition(), context))
                .orElseGet(() -> context.getMessage("standard.unknown"));
    }

    @Nullable
    private TextComponent getWalkingSpeed(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return user.get(Keys.WALKING_SPEED)
                .map(x -> context.getMessage("command.seen.speed.walk", NUMBER_FORMATTER.format(x * SpeedCommand.multiplier)))
                .orElse(null);
    }

    @Nullable
    private TextComponent getFlyingSpeed(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return user.get(Keys.FLYING_SPEED)
                .map(x -> context.getMessage("command.seen.speed.fly", NUMBER_FORMATTER.format(x * SpeedCommand.multiplier)))
                .orElse(null);
    }

    @Nullable
    private TextComponent getCanFly(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return context.getMessage("command.seen.canfly", getYesNo(user.get(Keys.CAN_FLY).orElse(false), context));
    }

    @Nullable
    private TextComponent getIsFlying(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return context.getMessage("command.seen.isflying", getYesNo(user.get(Keys.IS_FLYING).orElse(false), context));
    }

    @Nullable
    private TextComponent getGameMode(final ICommandContext context, final User user, final IUserDataObject userDataModule) {
        return user.get(Keys.GAME_MODE).map(x -> context.getMessage("command.seen.gamemode", x.getName())).orElse(null);
    }

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
            GenericArguments.firstParsing(
                NucleusParameters.ONE_USER_UUID.get(serviceCollection),
                NucleusParameters.ONE_USER.get(serviceCollection)
            )
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        User user = context.getOne(NucleusParameters.Keys.USER_UUID, User.class)
                .orElseGet(() -> context.requireOne(NucleusParameters.Keys.USER, User.class));
        // Get the player in case the User is displaying the wrong name.
        user = user.getPlayer().map(x -> (User) x).orElse(user);
        final IUserDataObject userDataObject = context.getServiceCollection()
                .storageManager()
                .getUserService()
                .getOrNewOnThread(user.getUniqueId());

        final List<Text> messages = new ArrayList<>();

        // Everyone gets the last online time.
        final IPlayerOnlineService playerOnlineService = context.getServiceCollection().playerOnlineService();
        if (playerOnlineService.isOnline(context.getCommandSourceRoot(), user)) {
            messages.add(context.getMessage("command.seen.iscurrently.online", user.getName()));
            userDataObject.get(CoreKeys.LAST_LOGIN).ifPresent(x -> messages.add(
                    context.getMessage("command.seen.loggedon", context.getTimeString(Duration.between(x, Instant.now())))));
        } else {
            messages.add(context.getMessage("command.seen.iscurrently.offline", user.getName()));
            playerOnlineService.lastSeen(context.getCommandSourceRoot(), user).ifPresent(x -> messages.add(
                    context.getMessage("command.seen.loggedoff", context.getTimeString(Duration.between(Instant.now(), x)))));
        }

        messages.add(context.getMessage("command.seen.displayname", context.getDisplayName(user.getUniqueId())));

        messages.add(Util.SPACE);
        for (final Map.Entry<String, TriFunction<ICommandContext, User, IUserDataObject, Text>> entry : this.entries.entrySet()) {
            if (context.testPermission(entry.getKey())) {
                @Nullable final TextComponent m = entry.getValue().accept(context, user, userDataObject);
                if (m != null) {
                    messages.add(m);
                }
            }
        }

        // Add the extra module information.
        // TODO: Ordering
        final IPlayerInformationService playerInformationService = context.getServiceCollection().playerInformationService();
        for (final IPlayerInformationService.Provider provider : playerInformationService.getProviders()) {
            provider.get(user, context.getCommandSourceRoot(), context.getServiceCollection()).ifPresent(messages::add);
        }

        messages.addAll(context.getServiceCollection().getServiceUnchecked(SeenHandler.class)
                .getText(context.getCommandSourceRoot(), user));

        Util.getPaginationBuilder(context.getCommandSourceRoot())
                .contents(messages)
                .padding(Text.of(TextColors.GREEN, "-"))
                .title(context.getMessage("command.seen.title", user.getName())).sendTo(context.getCommandSourceRoot());
        return context.successResult();
    }

    private TextComponent getLocationString(final String key, final Location<World> lw, final ICommandContext source) {
        return getLocationString(key, lw.getExtent().getProperties(), lw.getPosition(), source);
    }

    private TextComponent getLocationString(final String key, final WorldProperties worldProperties, final Vector3d position, final ICommandContext context) {
        final TextComponent text = context.getMessage(key, context.getMessage("command.seen.locationtemplate", worldProperties.getWorldName(),
                position.toInt().toString()));
        if (Sponge.getCommandManager().get("nucleus:tppos")
                .map(x -> x.getCallable().testPermission(context.getCommandSourceRoot()))
                .orElse(false)) {

            final Text.Builder building = text.toBuilder().onHover(TextActions.showText(
                    context.getMessage("command.seen.teleportposition")
            ));

            Sponge.getServer().getWorld(worldProperties.getUniqueId()).ifPresent(
                    x -> building.onClick(TextActions.executeCallback(cs -> {
                        if (cs instanceof Player) {
                            context.getServiceCollection().teleportService().setLocation((Player) cs, new Location<>(x, position));
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
