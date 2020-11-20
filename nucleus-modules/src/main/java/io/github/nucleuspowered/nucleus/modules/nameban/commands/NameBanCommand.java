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
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.CauseStackManager;
import java.util.stream.Collectors;

@Command(aliases = "nameban", basePermission = NameBanPermissions.BASE_NAMEBAN, commandDescriptionKey = "nameban")
public class NameBanCommand implements ICommandExecutor, IReloadableService.Reloadable {

    private final String nameKey = "name";

    private String defaultReason = "Your name is inappropriate";

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
            new RegexArgument(Text.of(this.nameKey),
                    Util.usernameRegexPattern, "command.nameban.notvalid", ((commandSource, commandArgs, commandContext) -> {
                try {
                    final String arg = commandArgs.peek().toLowerCase();
                    return Sponge.getServer().getOnlinePlayers().stream().filter(x -> x.getName().toLowerCase().startsWith(arg))
                        .map(User::getName)
                        .collect(Collectors.toList());
                } catch (final Exception e) {
                    return new ArrayList<>();
                }
            }), serviceCollection),
            NucleusParameters.OPTIONAL_REASON
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final String name = context.requireOne(this.nameKey, String.class).toLowerCase();
        final String reason = context.getOne(NucleusParameters.Keys.REASON, String.class).orElse(this.defaultReason);
        final NameBanHandler handler = context.getServiceCollection().getServiceUnchecked(NameBanHandler.class);

        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(context.getCommandSourceRoot());
                handler.addName(name, reason, frame.getCurrentCause());
                context.sendMessage("command.nameban.success", name);
                return context.successResult();
        } catch (final NameBanException ex) {
            ex.printStackTrace();
            return context.errorResult("command.nameban.failed", name);
        }
    }


    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.defaultReason = serviceCollection.configProvider().getModuleConfig(NameBanConfig.class).getDefaultReason();
    }
}
