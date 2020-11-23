/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nameban.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.api.module.nameban.exception.NameBanException;
import io.github.nucleuspowered.nucleus.modules.nameban.NameBanPermissions;
import io.github.nucleuspowered.nucleus.modules.nameban.services.NameBanHandler;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.scaffold.command.parameter.RegexParameter;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.regex.Pattern;

@Command(
        aliases = {"nameunban", "namepardon"},
        basePermission = NameBanPermissions.BASE_NAMEUNBAN,
        commandDescriptionKey = "nameunban")
public class NameUnbanCommand implements ICommandExecutor {

    private final Parameter.Value<String> regexParameter;

    public NameUnbanCommand(final INucleusServiceCollection serviceCollection) {
        this.regexParameter = Parameter.builder(String.class)
                .setKey("name")
                .parser(new RegexParameter(Pattern.compile(Util.USERNAME_REGEX_STRING), "command.nameban.notvalid", serviceCollection.messageProvider()))
                .build();
    }

    @Override public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            this.regexParameter
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String name = context.requireOne(this.regexParameter).toLowerCase();

        try {
            context.getServiceCollection().getServiceUnchecked(NameBanHandler.class).removeName(name);
            context.sendMessage("command.nameban.pardon.success", name);
            return context.successResult();
        } catch (final NameBanException ex) {
            ex.printStackTrace();
            return context.errorResult("command.nameban.pardon.failed", name);
        }
    }
}
