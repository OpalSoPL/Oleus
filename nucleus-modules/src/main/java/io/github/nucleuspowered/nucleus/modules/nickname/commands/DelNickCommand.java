/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nickname.commands;

import io.github.nucleuspowered.nucleus.api.module.nickname.exception.NicknameException;
import io.github.nucleuspowered.nucleus.modules.nickname.NicknamePermissions;
import io.github.nucleuspowered.nucleus.modules.nickname.services.NicknameService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;

@Command(
        aliases = {"delnick", "delnickname", "deletenick"},
        basePermission = NicknamePermissions.BASE_NICK,
        commandDescriptionKey = "delnick"
)
public class DelNickCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[]{
                serviceCollection.commandElementSupplier()
                        .createOnlyOtherUserPermissionElement(NicknamePermissions.OTHERS_NICK)
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User pl = context.getUserFromArgs();
        try {
            context.getServiceCollection().getServiceUnchecked(NicknameService.class).removeNick(pl.getUniqueId());
        } catch (final NicknameException e) {
            e.printStackTrace();
            return context.errorResultLiteral(e.componentMessage());
        }

        if (!context.is(pl)) {
            context.sendMessage("command.delnick.success.other", pl.getName());
        }

        return context.successResult();
    }
}
