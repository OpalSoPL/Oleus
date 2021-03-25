/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.kit.commands.command;

import io.github.nucleuspowered.nucleus.api.module.kit.data.Kit;
import io.github.nucleuspowered.nucleus.modules.kit.KitPermissions;
import io.github.nucleuspowered.nucleus.modules.kit.services.KitService;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;

import java.util.List;

@Command(
        aliases = { "remove", "del", "-" },
        basePermission = KitPermissions.BASE_KIT_COMMAND_REMOVE,
        commandDescriptionKey = "kit.command.remove",
        parentCommand = KitCommandCommand.class
)
public class KitRemoveCommandCommand implements ICommandExecutor {

    private final Parameter.Value<Integer> commands = Parameter.builder(Integer.class)
            .addParser(VariableValueParameters.integerRange().min(1).build())
            .key("index")
            .build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                serviceCollection.getServiceUnchecked(KitService.class).kitParameterWithoutPermission(),
                Parameter.firstOf(
                        this.commands,
                        NucleusParameters.COMMAND
                )
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final Kit kitInfo = context.requireOne(KitService.KIT_KEY);
        final List<String> commands = kitInfo.getCommands();

        final String cmd;
        if (context.hasAny(this.commands)) {
            final int idx = context.requireOne(this.commands);
            if (idx == 0) {
                return context.errorResult("command.kit.command.remove.onebased");
            }

            if (idx > commands.size()) {
                return context.errorResult("command.kit.command.remove.overidx", commands.size(), kitInfo.getName());
            }

            cmd = commands.remove(idx - 1);
        } else {
            cmd = context.requireOne(NucleusParameters.COMMAND).replace(" {player} ", " {{player}} ");
            if (!commands.remove(cmd)) {
                return context.errorResult("command.kit.command.remove.noexist", cmd, kitInfo.getName());
            }
        }

        kitInfo.setCommands(commands);
        context.getServiceCollection().getServiceUnchecked(KitService.class).saveKit(kitInfo);
        context.sendMessage("command.kit.command.remove.success", cmd, kitInfo.getName());
        return context.successResult();
    }
}
