/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.misc.commands;

import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.misc.MiscPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.util.blockray.RayTraceResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Command(
        aliases = "entityinfo",
        basePermission = MiscPermissions.BASE_ENTITYINFO,
        commandDescriptionKey = "entityinfo",
        associatedPermissions = MiscPermissions.ENTITYINFO_EXTENDED
)
public class EntityInfoCommand implements ICommandExecutor {

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.builder()
                        .setRequirement(x -> serviceCollection.permissionService().hasPermission(x, MiscPermissions.ENTITYINFO_EXTENDED))
                        .alias("e")
                        .alias("extended")
                        .build()
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        // Get all the entities in the world.
        final ServerPlayer player = context.requirePlayer();
        final RayTraceResult<Entity> rayTraceResult = RayTrace.entity()
                .select(e -> true)
                .continueWhileBlock(RayTrace.onlyAir())
                .sourceEyePosition(player)
                .direction(player)
                .execute()
                .orElseThrow(() -> context.createException("command.entityinfo.none"));


        // Display info about the entity
        final Entity entity = rayTraceResult.getSelectedObject();
        final EntityType<?> type = entity.getType();

        final List<Component> lt = new ArrayList<>();
        lt.add(context.getMessage("command.entityinfo.id", type.getKey().asString(), type.asComponent()));
        lt.add(context.getMessage("command.entityinfo.uuid", entity.getUniqueId().toString()));

        if (context.hasFlag("e")) {
            for (final Key<? extends Value<?>> key : entity.getKeys()) {
                final Optional<?> value = entity.get((Key) key); // this is the only way I could get this to work
                value.ifPresent(o -> lt.add(context.getMessage("command.entityinfo.key", key.getKey(), String.valueOf(o))));
            }
        }

        Util.getPaginationBuilder(context.getAudience())
                .contents(lt)
                .padding(Component.text("-", NamedTextColor.GREEN))
            .title(context.getMessage("command.entityinfo.list.header", entity.getPosition()))
            .sendTo(context.getAudience());

        return context.successResult();
    }

}
