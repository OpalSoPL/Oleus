/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.commands;

import io.github.nucleuspowered.nucleus.modules.admin.AdminPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.texttemplatefactory.NucleusTextTemplateMessageSender;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.command.parameter.Parameter;

@Command(aliases = {"tellplain", "plaintell", "ptell"},
        basePermission = AdminPermissions.BASE_TELLPLAIN,
        commandDescriptionKey = "tellplain")
public class TellPlainCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.MULTI_AUDIENCE,
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) {
        try {
            new NucleusTextTemplateMessageSender(
                    context.getServiceCollection().textTemplateFactory(),
                    context.getServiceCollection().textTemplateFactory().createFromAmpersandString(
                            context.requireOne(NucleusParameters.MESSAGE)),
                    context.getCommandSourceRoot())
                    .send(Audience.audience(context.requireOne(NucleusParameters.MULTI_AUDIENCE)), false);
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
            return context.errorResult("command.tellplain.failed");
        }
        return context.successResult();
    }
}
