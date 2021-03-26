/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.properties;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.modules.world.commands.WorldCommand;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;

@Command(
        aliases = {"sethardcore"},
        basePermission = WorldPermissions.BASE_WORLD_SETHARDCORE,
        commandDescriptionKey = "world.sethardcore",
        parentCommand = WorldCommand.class
)
public class SetHardcoreCommand extends AbstractPropertiesSetCommand {

    public SetHardcoreCommand() {
        super("hardcore");
    }

    @Override protected void setter(final ServerWorldProperties worldProperties, final boolean set) {
        worldProperties.setHardcore(set);
    }

    @Override protected void extraLogic(final ICommandContext context, final ServerWorldProperties worldProperties, final boolean set) {
        if (!set) {
            context.sendMessage("command.world.sethardcore.diff", worldProperties.difficulty().asComponent());
        }
    }
}
