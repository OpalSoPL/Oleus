/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ban.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.configurate.config.CommonPermissionLevelConfig;
import io.github.nucleuspowered.nucleus.modules.ban.BanPermissions;
import io.github.nucleuspowered.nucleus.modules.ban.config.BanConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.util.ban.Ban;

import java.util.Optional;

@Command(
        aliases = {"unban", "pardon"},
        basePermission = BanPermissions.BASE_TEMPBAN,
        commandDescriptionKey = "unban",
        associatedPermissionLevelKeys = BanPermissions.BAN_LEVEL_KEY
)
@EssentialsEquivalent({"unban", "pardon"})
public class UnbanCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private CommonPermissionLevelConfig levelConfig = new CommonPermissionLevelConfig();

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
            GenericArguments.firstParsing(
                    NucleusParameters.ONE_GAME_PROFILE_UUID.get(serviceCollection),
                    NucleusParameters.ONE_GAME_PROFILE.get(serviceCollection)
            )
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final GameProfile gp;
        if (context.hasAny(NucleusParameters.Keys.USER_UUID)) {
            gp = context.requireOne(NucleusParameters.Keys.USER_UUID, GameProfile.class);
        } else {
            gp = context.requireOne(NucleusParameters.Keys.USER, GameProfile.class);
        }

        final BanService service = Sponge.getServiceManager().provideUnchecked(BanService.class);

        final Optional<Ban.Profile> obp = service.getBanFor(gp);
        if (!obp.isPresent()) {
            return context.errorResult(
                    "command.checkban.notset", Util.getNameOrUnkown(context, gp));
        }

        final User user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).getOrCreate(gp);
        if (this.levelConfig.isUseLevels() &&
                !context.isPermissionLevelOkay(user,
                        BanPermissions.BAN_LEVEL_KEY,
                        BanPermissions.BASE_UNBAN,
                        this.levelConfig.isCanAffectSameLevel())) {
            // Failure.
            return context.errorResult("command.modifiers.level.insufficient", user.getName());
        }

        service.removeBan(obp.get());

        final MutableMessageChannel notify = context.getServiceCollection().permissionService().permissionMessageChannel(BanPermissions.BAN_NOTIFY).asMutable();
        notify.addMember(context.getCommandSourceRoot());
        for (final MessageReceiver receiver : notify.getMembers()) {
            context.sendMessageTo(receiver, "command.unban.success", Util.getNameOrUnkown(context, obp.get().getProfile()));
        }
        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.levelConfig = serviceCollection.configProvider().getModuleConfig(BanConfig.class).getLevelConfig();
    }
}
