/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.storage.WorldProperties;

@Command(
        aliases = {"rename"},
        basePermission = WorldPermissions.BASE_WORLD_RENAME,
        commandDescriptionKey = "world.rename",
        parentCommand = WorldCommand.class
)
public class RenameWorldCommand implements ICommandExecutor {

    private final Parameter.Value<String> nameKey = Parameter.string().setKey("new name").build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OFFLINE_WORLD,
                this.nameKey
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ResourceKey oldName = context.requireOne(NucleusParameters.OFFLINE_WORLD);
        final ResourceKey newName = ResourceKey.of("nucleus", context.requireOne(this.nameKey));
        Sponge.server().getWorldManager().moveWorld(oldName, newName).handle((result, exception) -> {
            context.getServiceCollection().schedulerService().runOnMainThread(() ->
            {
                if (exception == null && result) {
                    context.sendMessage("command.world.rename.success", oldName.asString(), newName.asString());
                } else {
                    context.sendMessage("command.world.rename.failed", oldName.asString(), newName.asString());
                }
            });
            return null;
        });

        return context.successResult();
    }
}
