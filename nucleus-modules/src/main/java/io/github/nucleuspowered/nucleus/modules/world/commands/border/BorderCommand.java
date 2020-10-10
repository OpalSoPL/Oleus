/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands.border;

import org.spongepowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.modules.world.commands.WorldCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.command.exception.CommandException;;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.List;

@Command(
        aliases = { "border" },
        basePermission = WorldPermissions.BASE_WORLD_BORDER,
        commandDescriptionKey = "world.border",
        parentCommand = WorldCommand.class
)
public class BorderCommand implements ICommandExecutor {

    @Override
    public CommandElement[] parameters(final INucleusServiceCollection serviceCollection) {
        return new CommandElement[] {
                NucleusParameters.OPTIONAL_WORLD_PROPERTIES_ENABLED_ONLY.get(serviceCollection)
        };
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        final WorldProperties wp = context.getWorldPropertiesOrFromSelfOptional(NucleusParameters.Keys.WORLD)
                .orElseThrow(() -> context.createException("command.world.player"));
        final List<Text> worldBorderInfo = Lists.newArrayList();

        final Vector3d centre = wp.getWorldBorderCenter();
        final int currentDiameter = (int)wp.getWorldBorderDiameter();
        final int targetDiameter = (int)wp.getWorldBorderTargetDiameter();

        // Border centre
        worldBorderInfo.add(context.getMessage("command.world.border.centre", String.valueOf(centre.getFloorX()), String.valueOf(centre.getFloorZ())));
        worldBorderInfo.add(context.getMessage("command.world.border.currentdiameter", String.valueOf(wp.getWorldBorderDiameter())));

        if (currentDiameter != targetDiameter) {
            worldBorderInfo.add(context.getMessage("command.world.border.targetdiameter", String.valueOf(targetDiameter), String.valueOf(wp.getWorldBorderTimeRemaining() / 1000)));
        }

        Util.getPaginationBuilder(context.getCommandSourceRoot())
                .contents(worldBorderInfo)
                .title(context.getMessage("command.world.border.title", wp.getWorldName()))
                .padding(Text.of(TextColors.GREEN, "="))
                .sendTo(context.getCommandSourceRoot());
        return context.successResult();
    }
}
