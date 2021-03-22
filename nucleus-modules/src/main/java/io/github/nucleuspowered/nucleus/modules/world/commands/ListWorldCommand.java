/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import net.kyori.adventure.text.Component;
import org.spongepowered.math.vector.Vector3i;
import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Command(
        aliases = {"list", "ls", "#worlds"},
        basePermission = WorldPermissions.BASE_WORLD_INFO,
        commandDescriptionKey = "world.list",
        parentCommand = WorldCommand.class,
        associatedPermissions = {
                WorldPermissions.WORLD_SEED
        }
)
public class ListWorldCommand implements ICommandExecutor {

    static void getWorldInfo(
            final ICommandContext context, final List<Component> listContent, final WorldProperties x, final boolean canSeeSeeds) {
        // Name of world
        if (!listContent.isEmpty()) {
            listContent.add(Component.space());
        }

        listContent.add(context.getMessage("command.world.list.worlditem", x.getKey().asString()));

        // As requested by Pixelmon for use in their config.
        // x.getAdditionalProperties().getInt(DataQuery.of("SpongeData", "dimensionId")).ifPresent(i ->
        //    listContent.add(context.getMessage("command.world.list.dimensionid", String.valueOf(i))));
        final Vector3i spawnPosition = x.getSpawnPosition();
        listContent.add(context.getMessage("command.world.list.spawnpoint",
                String.valueOf(spawnPosition.getX()), String.valueOf(spawnPosition.getY()), String.valueOf(spawnPosition.getZ())));

        listContent.add(context.getMessage("command.world.list.uuid", x.getUniqueId().toString()));
        if (x.isEnabled()) {
            final boolean worldLoaded = Sponge.server().getWorldManager().getWorld(x.getKey()).isPresent();
            final String message =
                (worldLoaded ? "&a" : "&c") + context.getMessageString(worldLoaded ? "standard.true" : "standard.false");
            listContent.add(context.getMessage("command.world.list.enabled", message));
        } else {
            listContent.add(context.getMessage("command.world.list.disabled"));
        }

        if (canSeeSeeds) {
            listContent.add(context.getMessage("command.world.list.seed", String.valueOf(x.getSeed())));
        }

        listContent.add(context.getMessage("command.world.list.params",
            x.getDimensionType().getKey().asString(),
            x.getGeneratorModifierType().getKey().asString(),
            x.getGameMode().getKey().asString(),
            x.getDifficulty().asComponent()));

        listContent.add(context.getMessage("command.world.list.params2",
            String.valueOf(x.isHardcore()),
            String.valueOf(x.doesLoadOnStartup()),
            String.valueOf(x.isPVPEnabled()),
            String.valueOf(x.doesKeepSpawnLoaded())
        ));
    }

    @Override public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Get all the worlds
        final Collection<WorldProperties> cwp = Sponge.server().getWorldManager().getAllProperties();
        final List<Component> listContent = new ArrayList<>();

        final boolean canSeeSeeds = context.testPermission(WorldPermissions.WORLD_SEED);
        cwp.stream().sorted(Comparator.comparing(x -> x.getKey().asString()))
                .forEach(x -> getWorldInfo(context, listContent, x, canSeeSeeds));

        Util.getPaginationBuilder(context.getAudience())
            .contents(listContent).title(context.getMessage("command.world.list.title"))
            .sendTo(context.getAudience());

        return context.successResult();
    }
}
