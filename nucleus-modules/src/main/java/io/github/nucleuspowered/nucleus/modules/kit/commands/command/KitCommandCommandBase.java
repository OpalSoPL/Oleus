package io.github.nucleuspowered.nucleus.modules.kit.commands.command;

import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.modules.kit.config.KitConfig;
import org.spongepowered.api.command.exception.CommandException;

public abstract class KitCommandCommandBase implements ICommandExecutor {

    @Override
    public final ICommandResult execute(final ICommandContext context) throws CommandException {
        if (context.getServiceCollection().configProvider().getModuleConfig(KitConfig.class).isEnableKitCommands()) {
            return this.execute0(context);
        }
        return context.errorResult("command.kit.command.disabled");
    }

    protected abstract ICommandResult execute0(final ICommandContext context) throws CommandException;

}
