/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.info.commands;

import io.github.nucleuspowered.nucleus.io.TextFileController;
import io.github.nucleuspowered.nucleus.modules.info.InfoModule;
import io.github.nucleuspowered.nucleus.modules.info.InfoPermissions;
import io.github.nucleuspowered.nucleus.modules.info.config.InfoConfig;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import java.util.Optional;

@Command(
        aliases = {"motd"},
        async = true,
        basePermission = InfoPermissions.BASE_MOTD,
        commandDescriptionKey = "motd"
)
@EssentialsEquivalent("motd")
public class MotdCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private TextComponent title = Text.EMPTY;
    private boolean usePagination = true;

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Optional<TextFileController> otfc = context.getServiceCollection().textFileControllerCollection().get(InfoModule.MOTD_KEY);
        if (!otfc.isPresent()) {
            return context.errorResult("command.motd.nocontroller");
        }

        final CommandSource src = context.getCommandSourceRoot();
        if (this.usePagination) {
            otfc.get().sendToAudience(src, this.title);
        } else {
            otfc.get().getTextFromNucleusTextTemplates(src).forEach(src::sendMessage);
        }

        return context.successResult();
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        final InfoConfig config = serviceCollection.configProvider().getModuleConfig(InfoConfig.class);
        final String title = config.getMotdTitle();
        if (title.isEmpty()) {
            this.title = Text.EMPTY;
        } else {
            this.title = TextSerializers.FORMATTING_CODE.deserialize(title);
        }

        this.usePagination = config.isMotdUsePagination();
    }
}
