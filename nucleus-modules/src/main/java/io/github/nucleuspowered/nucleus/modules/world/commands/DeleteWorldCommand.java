/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.world.commands;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.core.Util;
import io.github.nucleuspowered.nucleus.modules.world.WorldPermissions;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.core.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.core.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.core.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.vavr.Tuple3;
import net.kyori.adventure.audience.Audience;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.storage.ServerWorldProperties;
import org.spongepowered.api.world.storage.WorldProperties;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Command(
        aliases = {"delete", "del"},
        basePermission = WorldPermissions.BASE_WORLD_DELETE,
        commandDescriptionKey = "world.delete",
        parentCommand = WorldCommand.class
)
public class DeleteWorldCommand implements ICommandExecutor {

    @Nullable private Tuple3<Instant, UUID, ServerWorldProperties> confirm = null;

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                NucleusParameters.ONLINE_WORLD,
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final ServerWorld properties = context.requireOne(NucleusParameters.ONLINE_WORLD);
        if (this.confirm != null && this.confirm._1().isAfter(Instant.now()) &&
                this.confirm._2().equals(context.getUniqueId().orElse(Util.CONSOLE_FAKE_UUID)) &&
                this.confirm._3().getUniqueId().equals(properties.getUniqueId())) {
            try {
                return this.completeDeletion(context, properties.getProperties());
            } finally {
                this.confirm = null;
            }
        }

        // Scary warning.
        this.confirm = new Tuple3<>(Instant.now().plus(30, ChronoUnit.SECONDS), context.getUniqueId().orElse(Util.CONSOLE_FAKE_UUID), properties.getProperties());
        context.sendMessage("command.world.delete.warning1", properties.getKey().asString());
        context.sendMessage("command.world.delete.warning3", properties.getKey().asString());
        return context.successResult();
    }

    private ICommandResult completeDeletion(final ICommandContext context, final ServerWorldProperties properties) throws CommandException {
        Preconditions.checkNotNull(this.confirm);
        final String worldName = this.confirm._3().getKey().asString();
        if (Sponge.server().getWorldManager().world(properties.getKey()).isPresent()) {
            return context.errorResult("command.world.delete.loaded", this.confirm._3().getKey().asString());
        }

        final SystemSubject consoleSource = Sponge.getSystemSubject();
        context.sendMessage("command.world.delete.confirmed", worldName);
        if (!context.is(consoleSource)) {
            context.sendMessageTo(consoleSource, "command.world.delete.confirmed", worldName);
        }

        // Now request deletion
        final CompletableFuture<Boolean> completableFuture = Sponge.server().getWorldManager().deleteWorld(properties.getKey());
        final Supplier<Optional<? extends Audience>> source;
        if (context.getAudience() instanceof ServerPlayer) {
            final UUID uuid = ((ServerPlayer) context.getAudience()).getUniqueId();
            source = () -> Sponge.server().getPlayer(uuid);
        } else {
            source = Optional::empty;
        }

        completableFuture.handle((result, exception) -> {
            if (exception != null || !result) {
                if (exception != null) {
                    exception.printStackTrace();
                }
                source.get().ifPresent(x -> {
                    context.sendMessageTo(x, "command.world.delete.complete.error", worldName);
                });

                context.sendMessageTo(consoleSource, "command.world.delete.complete.error", worldName);
            } else {
                source.get().ifPresent(x -> context.sendMessageTo(x, "command.world.delete.complete.success", worldName));
                context.sendMessageTo(consoleSource, "command.world.delete.complete.success", worldName);
            }
            return null;
        });

        return context.successResult();
    }

}
