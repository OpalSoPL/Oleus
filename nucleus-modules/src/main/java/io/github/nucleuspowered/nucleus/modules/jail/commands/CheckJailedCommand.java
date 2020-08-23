/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.jail.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import io.github.nucleuspowered.nucleus.modules.jail.JailParameters;
import io.github.nucleuspowered.nucleus.modules.jail.JailPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IUserCacheService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Command(
        aliases = "checkjailed",
        basePermission = JailPermissions.BASE_CHECKJAILED,
        async = true,
        commandDescriptionKey = "checkjailed"
)
public class CheckJailedCommand implements ICommandExecutor {

    @Override public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                JailParameters.OPTIONAL_JAIL.get(serviceCollection)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Using the cache, tell us who is jailed.
        final Optional<NamedLocation> jail = context.getOne(JailParameters.JAIL_KEY, NamedLocation.class);

        //
        final IUserCacheService userCacheService = context.getServiceCollection().userCacheService();
        final List<UUID> usersInJail = jail.map(x -> userCacheService.getJailedIn(x.getName()))
                .orElseGet(userCacheService::getJailed);
        //

        final String jailName = jail.map(NamedLocation::getName).orElseGet(() -> context.getMessageString("standard.alljails"));

        if (usersInJail.isEmpty()) {
            context.sendMessage("command.checkjailed.none", jailName);
            return context.successResult();
        }

        final CommandSource src = context.getCommandSourceRoot();
        // Get the users in this jail, or all jails
        Util.getPaginationBuilder(src)
            .title(context.getMessage("command.checkjailed.header", jailName))
            .contents(usersInJail.stream().map(x -> {
                TextComponent name;
                        try {
                            name = context.getDisplayName(x);
                        } catch (final IllegalArgumentException ex) {
                            name = Text.of("unknown: ", x.toString());
                        }
                return name.toBuilder()
                    .onHover(TextActions.showText(context.getMessage("command.checkjailed.hover")))
                    .onClick(TextActions.runCommand("/nucleus:checkjail " + x.toString()))
                    .build();
            }).collect(Collectors.toList())).sendTo(src);
        return context.successResult();
    }
}
