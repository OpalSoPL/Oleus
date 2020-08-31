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
import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateMessageSender;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.TypeTokens;
@Command(aliases = {"broadcast", "bcast", "bc"}, basePermission = NotificationPermissions.BASE_BROADCAST, commandDescriptionKey = "broadcast")
@EssentialsEquivalent({"broadcast", "bcast"})
public class BroadcastCommand implements ICommandExecutor, IReloadableService.Reloadable {
    private BroadcastConfig bc = new BroadcastConfig();

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.MESSAGE
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String m = context.requireOne(NucleusParameters.Keys.MESSAGE, TypeTokens.STRING_TOKEN);

        final TextComponent p = this.bc.getPrefix().getForObject(context.getCommandSourceRoot());
        final TextComponent s = this.bc.getSuffix().getForObject(context.getCommandSourceRoot());

        final NucleusTextTemplate textTemplate =
                context.getServiceCollection().textTemplateFactory()
                        .createFromAmpersandString(m, p, s);

        new NucleusTextTemplateMessageSender(
                context.getServiceCollection().textTemplateFactory(),
                textTemplate,
                context.getServiceCollection().placeholderService(),
                context.getCommandSourceRoot()
        ).send(context.getCause());
        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        this.bc = serviceCollection
                .configProvider()
                .getModuleConfig(NotificationConfig.class)
                .getBroadcastMessage();
    }
}
