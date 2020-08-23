/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ignore.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.ignore.IgnorePermissions;
import io.github.nucleuspowered.nucleus.modules.ignore.services.IgnoreService;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import java.util.List;
import java.util.stream.Collectors;

@Command(
        aliases = {"ignorelist", "listignore", "ignored"},
        basePermission = IgnorePermissions.BASE_IGNORELIST,
        commandDescriptionKey = "ignorelist",
        async = true,
        associatedPermissions = IgnorePermissions.OTHERS_IGNORELIST
)
public class IgnoreListCommand implements ICommandExecutor {

    @Override public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                serviceCollection.commandElementSupplier().createOnlyOtherUserPermissionElement(false, IgnorePermissions.OTHERS_IGNORELIST)
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final IgnoreService ignoreService = context.getServiceCollection().getServiceUnchecked(IgnoreService.class);
        final User target = context.getPlayerFromArgs();
        final boolean isSelf = context.is(target);
        final IPlayerDisplayNameService playerDisplayNameService = context.getServiceCollection().playerDisplayNameService();

        final List<Text> ignoredList = ignoreService
                .getAllIgnored(target.getUniqueId())
                .stream()
                .map(playerDisplayNameService::getDisplayName)
                .collect(Collectors.toList());

        if (ignoredList.isEmpty()) {
            if (isSelf) {
                context.sendMessage("command.ignorelist.noignores.self");
            } else {
                context.sendMessage("command.ignorelist.noignores.other", target);
            }
        } else {
            Util.getPaginationBuilder(context.getCommandSourceRoot())
                    .contents(ignoredList)
                    .title(context.getMessage("command.ignorelist.header", target))
                    .sendTo(context.getCommandSourceRoot());
        }
        return context.successResult();
    }

}
