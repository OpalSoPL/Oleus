/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.border;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.modules.world.commands.WorldCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

@Command(
        aliases = { "border" },
        basePermission = WorldPermissions.BASE_WORLD_BORDER,
        commandDescriptionKey = "world.border",
        parentCommand = WorldCommand.class
)
public class BorderCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties wp = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY.getKey())
                .orElseThrow(() -> context.createException("command.world.player"));
        final List<Component> worldBorderInfo = new ArrayList<>();

        final WorldBorder worldBorder = wp.getWorldBorder();
        final Vector3d centre = worldBorder.getCenter();
        final int currentDiameter = (int) worldBorder.getDiameter();
        final int targetDiameter = (int) worldBorder.getNewDiameter();

        // Border centre
        worldBorderInfo.add(context.getMessage("command.world.border.centre", String.valueOf(centre.getFloorX()), String.valueOf(centre.getFloorZ())));
        worldBorderInfo.add(context.getMessage("command.world.border.currentdiameter", currentDiameter));

        if (currentDiameter != targetDiameter) {
            worldBorderInfo.add(context.getMessage("command.world.border.targetdiameter", targetDiameter,
                    String.valueOf(worldBorder.getTimeRemaining().getSeconds())));
        }

        Util.getPaginationBuilder(context.getAudience())
                .contents(worldBorderInfo)
                .title(context.getMessage("command.world.border.title", wp.getKey().asString()))
                .padding(Component.text("=", NamedTextColor.GREEN))
                .sendTo(context.getAudience());
        return context.successResult();
    }
}
