/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.lists;

import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.modules.world.commands.WorldCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.world.gen.GeneratorModifierType;

@Command(
        aliases = {"modifiers", "listmodifiers"},
        basePermission = WorldPermissions.BASE_WORLD_CREATE,
        commandDescriptionKey = "world.modifiers",
        parentCommand = WorldCommand.class
)
public class AvailableModifiersCommand extends AvailableBaseCommand<GeneratorModifierType> {

    public AvailableModifiersCommand() {
        super(GeneratorModifierType.class, "command.world.modifiers.title");
    }

    @Override
    protected Component retrieveName(final GeneratorModifierType type) {
        return Component.text(type.getKey().asString());
    }
}
