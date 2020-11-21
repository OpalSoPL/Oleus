/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.notification.command;

import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.modules.notification.NotificationPermissions;
import io.github.nucleuspowered.nucleus.modules.notification.config.BroadcastConfig;
import io.github.nucleuspowered.nucleus.modules.notification.config.NotificationConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateMessageSender;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;

@Command(aliases = {"broadcast", "bcast", "bc"}, basePermission = NotificationPermissions.BASE_BROADCAST, commandDescriptionKey = "broadcast")
@EssentialsEquivalent({"broadcast", "bcast"})
public class BroadcastCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private NucleusTextTemplate prefix = NucleusTextTemplateImpl.empty();
    private NucleusTextTemplate suffix = NucleusTextTemplateImpl.empty();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String m = context.requireOne(NucleusParameters.MESSAGE);

        final Component p = this.prefix.getForObject(context.getCommandSourceRoot());
        final Component s = this.suffix.getForObject(context.getCommandSourceRoot());

        final NucleusTextTemplate textTemplate =
                context.getServiceCollection().textTemplateFactory()
                        .createFromAmpersandString(m, p, s);

        new NucleusTextTemplateMessageSender(
                context.getServiceCollection().textTemplateFactory(),
                textTemplate,
                context.getCommandSourceRoot()
        ).send(context.getCause().getAudience());
        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        final BroadcastConfig config = serviceCollection
                .configProvider()
                .getModuleConfig(NotificationConfig.class)
                .getBroadcastMessage();
        this.prefix = serviceCollection.textTemplateFactory().createFromAmpersandString(config.getPrefix());
        this.suffix = serviceCollection.textTemplateFactory().createFromAmpersandString(config.getSuffix());
    }
}
