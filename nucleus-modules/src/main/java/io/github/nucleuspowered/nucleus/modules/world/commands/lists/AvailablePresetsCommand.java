/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.lists;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.modules.world.commands.WorldCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.world.WorldArchetype;

@Command(
        aliases = {"presets", "listpresets"},
        basePermission = WorldPermissions.BASE_WORLD_CREATE,
        commandDescriptionKey = "world.presets",
        parentCommand = WorldCommand.class
)
public class AvailablePresetsCommand extends AvailableBaseCommand<WorldArchetype> {

    public AvailablePresetsCommand() {
        super(WorldArchetype.class, "command.world.presets.title");
    }

    @Override
    protected Component retrieveName(final WorldArchetype type) {
        return Component.text(type.getKey().asString());
    }
}
