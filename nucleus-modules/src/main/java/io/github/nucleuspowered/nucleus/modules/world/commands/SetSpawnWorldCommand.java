/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import org.spongepowered.math.vector.Vector3i;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.storage.WorldProperties;

@Command(
        aliases = {"setspawn"},
        basePermission = WorldPermissions.BASE_WORLD_SETSPAWN,
        commandDescriptionKey = "world.setspawn",
        parentCommand = WorldCommand.class
)
public class SetSpawnWorldCommand implements ICommandExecutor {

    private final String xKey = "x";
    private final String yKey = "y";
    private final String zKey = "z";

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                GenericArguments.optional(
                        GenericArguments.seq(
                                NucleusParameters.WORLD_PROPERTIES_ENABLED_ONLY.get(serviceCollection),
                                GenericArguments.integer(Text.of(this.xKey)),
                                GenericArguments.integer(Text.of(this.yKey)),
                                GenericArguments.integer(Text.of(this.zKey))
                        )
                )
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties world = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.Keys.WORLD)
                .orElseThrow(() -> context.createException("command.world.player"));
        final Vector3i loc;
        if (context.hasAny(this.xKey)) {
            loc = new Vector3i(
                    context.requireOne(this.xKey, Integer.class),
                    context.requireOne(this.yKey, Integer.class),
                    context.requireOne(this.zKey, Integer.class)
            );
        } else {
            loc = ((Locatable) context.getCommandSourceRoot()).getLocation().getBlockPosition();
        }

        world.setSpawnPosition(loc);
        context.sendMessage("command.world.setspawn.success");
        return context.successResult();
    }
}
