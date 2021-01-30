/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.border;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;

import java.time.Duration;

@Command(
        aliases = { "set" },
        basePermission = WorldPermissions.BASE_BORDER_SET,
        commandDescriptionKey = "world.border.set",
        parentCommand = BorderCommand.class
)
public class SetBorderCommand implements ICommandExecutor {

    private final Parameter.Value<Integer> xParam = Parameter.integerNumber().setKey("x").build();
    private final Parameter.Value<Integer> zParam = Parameter.integerNumber().setKey("z").build();
    private final Parameter.Value<Integer> diameterParameter = Parameter.rangedInteger(1, Integer.MAX_VALUE).setKey("diameter").build();
    private final Parameter.Value<Duration> durationParameter = Parameter.duration().setKey("delay").optional().build();

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                Parameter.seqBuilder(NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ALL)
                    .then(this.xParam)
                    .then(this.zParam)
                    .optional()
                    .build(),
                this.diameterParameter,
                this.durationParameter
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties wp = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ALL.getKey())
                .orElseThrow(() -> context.createException("command.world.player"));
        final int x;
        final int z;
        final int dia = context.requireOne(this.diameterParameter);
        final Duration delay = context.getOne(this.durationParameter).orElse(Duration.ZERO);

        if (context.is(Locatable.class)) {
            final ServerLocation lw = ((Locatable) context.getCommandSourceRoot()).getServerLocation();
            if (context.hasAny(this.zParam)) {
                x = context.requireOne(this.xParam);
                z = context.requireOne(this.zParam);
            } else {
                x = lw.getBlockX();
                z = lw.getBlockZ();
            }
        } else {
            x = context.requireOne(this.xParam);
            z = context.requireOne(this.zParam);
        }

        final WorldBorder border = wp.getWorldBorder();
        // Now, if we have an x and a z key, get the centre from that.
        border.setCenter(x, z);

        if (delay == Duration.ZERO) {
            border.setDiameter(dia);
            context.sendMessage("command.world.setborder.set",
                    wp.getKey().asString(),
                    String.valueOf(x),
                    String.valueOf(z),
                    String.valueOf(dia));
        } else {
            border.setDiameter(dia, delay);
            context.sendMessage("command.world.setborder.setdelay",
                    wp.getKey().asString(),
                    String.valueOf(x),
                    String.valueOf(z),
                    String.valueOf(dia),
                    String.valueOf(delay));
        }

        return context.successResult();
    }


}
