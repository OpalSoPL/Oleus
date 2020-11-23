/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rules.commands;

import io.github.nucleuspowered.nucleus.modules.rules.RulesModule;
import io.github.nucleuspowered.nucleus.modules.rules.RulesPermissions;
import io.github.nucleuspowered.nucleus.modules.rules.config.RulesConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.command.exception.CommandException;

@EssentialsEquivalent("rules")
@Command(aliases = "rules", basePermission = RulesPermissions.BASE_RULES, commandDescriptionKey = "rules")
public class RulesCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private Component title = Component.empty();

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        context.getServiceCollection()
                .textFileControllerCollection()
                .get(RulesModule.ID)
                .orElseThrow(() -> context.createException("command.rules.empty"))
                .sendToAudience(context.getAudience(), this.title);
        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        final RulesConfig config = serviceCollection.configProvider().getModuleConfig(RulesConfig.class);
        final String title = config.getRulesTitle();
        if (title.isEmpty()) {
            this.title = Component.empty();
        } else {
            this.title = LegacyComponentSerializer.legacyAmpersand().deserialize(title);
        }
    }
}
