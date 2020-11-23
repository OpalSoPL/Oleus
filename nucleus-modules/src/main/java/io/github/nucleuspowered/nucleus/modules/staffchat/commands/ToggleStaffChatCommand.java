/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.staffchat.commands;

import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatKeys;
import io.github.nucleuspowered.nucleus.modules.staffchat.StaffChatPermissions;
import io.github.nucleuspowered.nucleus.modules.staffchat.services.StaffChatService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IUserPreferenceService;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@Command(
        aliases = {"toggleviewstaffchat", "vsc", "togglevsc"},
        basePermission = StaffChatPermissions.BASE_STAFFCHAT,
        commandDescriptionKey = "toggleviewstaffchat"
)
public class ToggleStaffChatCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_ONE_TRUE_FALSE
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerPlayer src = context.requirePlayer();
        final IUserPreferenceService ups = context.getServiceCollection().userPreferenceService();
        final boolean result =
                context.getOne(NucleusParameters.OPTIONAL_ONE_TRUE_FALSE).orElseGet(() ->
                    ups.getPreferenceFor(src.getUniqueId(), StaffChatKeys.VIEW_STAFF_CHAT).orElse(true));
        ups.setPreferenceFor(src.getUniqueId(), StaffChatKeys.VIEW_STAFF_CHAT, !result);
        final StaffChatService service = context.getServiceCollection().getServiceUnchecked(StaffChatService.class);

        if (!result && service.isToggledChat(src)) {
            service.toggle(src, false);
        }

        context.sendMessage("command.staffchat.view." + (result ? "on" : "off"));
        return context.successResult();
    }

}
