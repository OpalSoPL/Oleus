/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.teleport.commands;

import io.github.nucleuspowered.nucleus.api.teleport.data.TeleportScanners;
import io.github.nucleuspowered.nucleus.modules.teleport.TeleportPermissions;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.EssentialsEquivalent;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.World;
import org.spongepowered.math.vector.Vector3d;

@EssentialsEquivalent("tpall")
@Command(aliases = {"tpall", "tpallhere"}, basePermission = TeleportPermissions.BASE_TPALL, commandDescriptionKey = "tpall")
public class TeleportAllHereCommand implements ICommandExecutor {

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                GenericArguments.flags().flag("f").buildWith(GenericArguments.none())
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        MessageChannel.TO_ALL.getMembers()
                .forEach(x -> context.sendMessageTo(x, "command.tpall.broadcast", context.getName()));
        final Transform<World> toTransform = context.getIfPlayer().getTransform();
        Sponge.getServer().getOnlinePlayers().forEach(x -> {
            if (!context.is(x)) {
                context.getServiceCollection()
                        .teleportService()
                        .teleportPlayerSmart(x,
                            toTransform,
                                Vector3d.ZERO, false,
                            !context.getOne("f", Boolean.class).orElse(false),
                            TeleportScanners.NO_SCAN.get()
                );
            }
        });

        return context.successResult();
    }
}
