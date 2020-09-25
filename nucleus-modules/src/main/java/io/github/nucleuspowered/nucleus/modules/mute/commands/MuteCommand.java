/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.mute.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.configurate.config.CommonPermissionLevelConfig;
import io.github.nucleuspowered.nucleus.modules.mute.MutePermissions;
import io.github.nucleuspowered.nucleus.modules.mute.config.MuteConfig;
import io.github.nucleuspowered.nucleus.modules.mute.data.MuteData;
import io.github.nucleuspowered.nucleus.modules.mute.services.MuteHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@EssentialsEquivalent(value = {"mute", "silence"}, isExact = false, notes = "Unmuting a player should be done via the /unmute command.")
@Command(
        aliases = { "mute" },
        basePermission = MutePermissions.BASE_MUTE,
        commandDescriptionKey = "mute",
        associatedPermissionLevelKeys = MutePermissions.MUTE_LEVEL_KEY,
        associatedPermissions = {
                MutePermissions.MUTE_EXEMPT_LENGTH,
                MutePermissions.MUTE_EXEMPT_TARGET,
                MutePermissions.MUTE_NOTIFY,
                MutePermissions.MUTE_SEEMUTEDCHAT
        }
)
public class MuteCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private long maxMute = Long.MAX_VALUE;
    private CommonPermissionLevelConfig levelConfig = new CommonPermissionLevelConfig();

    // seemuted

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
            NucleusParameters.ONE_USER.get(serviceCollection),
            NucleusParameters.OPTIONAL_WEAK_DURATION.get(serviceCollection),
            NucleusParameters.OPTIONAL_REASON
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final MuteHandler handler = context.getServiceCollection().getServiceUnchecked(MuteHandler.class);
        // Get the user.
        final User user = context.requireOne(NucleusParameters.Keys.USER, User.class);
                //args.<User>getOne(NucleusParameters.Keys.USER).get();

        final Optional<Long> time = context.getOne(NucleusParameters.Keys.DURATION, Long.class);
        final Optional<MuteData> omd = handler.getPlayerMuteData(user);
        final Optional<String> reas = context.getOne(NucleusParameters.Keys.REASON, String.class);

        if (!context.isConsoleAndBypass() && context.testPermissionFor(user, MutePermissions.MUTE_EXEMPT_TARGET)) {
            return context.errorResult("command.mute.exempt", user.getName());
        }

        if (this.levelConfig.isUseLevels() &&
                !context.isPermissionLevelOkay(user,
                        MutePermissions.MUTE_LEVEL_KEY,
                        MutePermissions.BASE_MUTE,
                        this.levelConfig.isCanAffectSameLevel())) {
            // Failure.
            return context.errorResult("command.modifiers.level.insufficient", user.getName());
        }

        // Do we have a reason?
        final String rs = reas.orElseGet(() -> context.getMessageString("command.mute.defaultreason"));
        UUID ua = Util.CONSOLE_FAKE_UUID;
        if (context.is(Player.class)) {
            ua = context.getIfPlayer().getUniqueId();
        }

        if (this.maxMute > 0 && time.orElse(Long.MAX_VALUE) > this.maxMute && !context.testPermission(MutePermissions.MUTE_EXEMPT_TARGET)) {
            return context.errorResult("command.mute.length.toolong", context.getTimeString(this.maxMute));
        }

        final MuteData data;
        if (time.isPresent()) {
            if (!user.isOnline()) {
                data = new MuteData(ua, rs, Duration.ofSeconds(time.get()));
            } else {
                data = new MuteData(ua, rs, Instant.now().plus(time.get(), ChronoUnit.SECONDS));
            }
        } else {
            data = new MuteData(ua, rs);
        }

        final CommandSource src = context.getCommandSourceRoot();
        if (handler.mutePlayer(user, data)) {
            // Success.
            final MutableMessageChannel mc =
                    context.getServiceCollection().permissionService().permissionMessageChannel(MutePermissions.MUTE_NOTIFY).asMutable();
            mc.addMember(src);

            if (time.isPresent()) {
                timedMute(context, user, data, time.get(), mc);
            } else {
                permMute(context, user, data, mc);
            }

            return context.successResult();
        }

        return context.errorResult("command.mute.fail", user.getName());
    }

    private void timedMute(
            final ICommandContext context, final User user, final MuteData data, final long time, final MessageChannel mc) {
        final String ts = context.getTimeString(time);
        mc.send(context.getMessage("command.mute.success.time", user.getName(), context.getName(), ts));
        mc.send(context.getMessage("standard.reasoncoloured", data.getReason()));

        if (user.isOnline()) {
            context.sendMessageTo(user.getPlayer().get(), "mute.playernotify.time", ts);
            context.sendMessageTo(user.getPlayer().get(), "command.reason", data.getReason());
        }
    }

    private void permMute(final ICommandContext context, final User user, final MuteData data, final MessageChannel mc) {
        mc.send(context.getMessage("command.mute.success.norm", user.getName(), context.getName()));
        mc.send(context.getMessage("standard.reasoncoloured", data.getReason()));

        if (user.isOnline()) {
            context.sendMessageTo(user.getPlayer().get(), "mute.playernotify.standard");
            context.sendMessageTo(user.getPlayer().get(), "command.reason", data.getReason());
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        final MuteConfig config = serviceCollection.configProvider().getModuleConfig(MuteConfig.class);
        this.maxMute = config.getMaximumMuteLength();
        this.levelConfig = config.getLevelConfig();
    }
}
