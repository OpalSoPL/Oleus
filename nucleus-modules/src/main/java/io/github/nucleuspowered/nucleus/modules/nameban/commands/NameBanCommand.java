/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.nameban.commands;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.api.module.nameban.exception.NameBanException;
import io.github.nucleuspowered.nucleus.modules.nameban.NameBanPermissions;
import io.github.nucleuspowered.nucleus.modules.nameban.config.NameBanConfig;
import io.github.nucleuspowered.nucleus.modules.nameban.services.NameBanHandler;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.RegexParameter;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.regex.Pattern;

@Command(aliases = "nameban", basePermission = NameBanPermissions.BASE_NAMEBAN, commandDescriptionKey = "nameban")
public class NameBanCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final Parameter.Value<String> regexParameter;

    public NameBanCommand(final INucleusServiceCollection serviceCollection) {
        this.regexParameter = Parameter.builder(String.class)
                .setKey("name")
                .parser(new RegexParameter(Pattern.compile(Util.USERNAME_REGEX_STRING), "command.nameban.notvalid", serviceCollection.messageProvider()))
                .build();
    }

    private String defaultReason = "Your name is inappropriate";

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            this.regexParameter,
            NucleusParameters.OPTIONAL_REASON
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String name = context.requireOne(this.regexParameter).toLowerCase();
        final String reason = context.getOne(NucleusParameters.OPTIONAL_REASON).orElse(this.defaultReason);
        final NameBanHandler handler = context.getServiceCollection().getServiceUnchecked(NameBanHandler.class);

        try {
            handler.addName(name, reason);
            context.sendMessage("command.nameban.success", name);
            return context.successResult();
        } catch (final NameBanException ex) {
            return context.errorResult("command.nameban.failed", name);
        }
    }


    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.defaultReason = serviceCollection.configProvider().getModuleConfig(NameBanConfig.class).getDefaultReason();
    }
}
