/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.commands.nucleus;

import io.github.nucleuspowered.nucleus.Util;
import io.github.nucleuspowered.nucleus.core.CorePermissions;
import io.github.nucleuspowered.nucleus.core.commands.NucleusCommand;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.NucleusParameters;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.Command;
import io.github.nucleuspowered.nucleus.scaffold.command.parameter.UUIDParameter;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.queryobjects.IUserQueryObject;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.storage.services.IStorageService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ban.Ban;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.ban.BanTypes;
import org.spongepowered.api.util.Ticks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Command(
        aliases = "resetuser",
        basePermission = CorePermissions.BASE_NUCLEUS_RESETUSER,
        commandDescriptionKey = "nucleus.resetuser",
        parentCommand = NucleusCommand.class,
        )
public class ResetUserCommand implements ICommandExecutor {

    private final String userKey = "user";
    private final String uuidKey = "UUID";

    private final Map<UUID, Delete> callbacks = new HashMap<>();

    @Override
    public Flag[] flags(final INucleusServiceCollection serviceCollection) {
        return new Flag[] {
                Flag.of("a", "all")
        };
    }

    @Override
    public Parameter[] parameters(final INucleusServiceCollection serviceCollection) {
        return new Parameter[] {
                Parameter.firstOf(
                        NucleusParameters.ONE_USER,
                        Parameter.builder(User.class)
                                .setKey(NucleusParameters.ONE_USER.getKey())
                                .parser(UUIDParameter.user(serviceCollection.messageProvider()))
                                .build()
                )
        };
    }

    @Override
    public ICommandResult execute(final ICommandContext context) throws CommandException {
        final User user = context.requireOne(NucleusParameters.ONE_USER);
        final boolean deleteall = context.hasFlag("a");
        final UUID responsible = context.getUniqueId().orElse(Util.CONSOLE_FAKE_UUID);

        final Audience targetAudience = context.getAudience();
        if (this.callbacks.containsKey(responsible)) {
            final Delete delete = this.callbacks.get(responsible);
            this.callbacks.remove(responsible);
            if (Instant.now().isBefore(delete.until) && delete.all == deleteall && delete.user.equals(user.getUniqueId())) {
                // execute that callback
                delete.accept(targetAudience);
                return context.successResult();
            }
        }

        this.callbacks.values().removeIf(x -> Instant.now().isAfter(x.until));
        this.callbacks.remove(responsible);
        final List<Component> messages = new ArrayList<>();

        final IMessageProviderService messageProvider = context.getServiceCollection().messageProvider();

        messages.add(messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.warning"));
        messages.add(messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.warning2", user.getName()));
        messages.add(messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.warning3"));
        messages.add(messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.warning4"));
        messages.add(messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.warning5"));
        messages.add(messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.warning6"));
        if (deleteall) {
            messages.add(messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.warning8"));
        } else {
            messages.add(messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.warning7"));
        }

        this.callbacks.put(responsible,
                new Delete(Instant.now().plus(30, ChronoUnit.SECONDS),
                    user.getUniqueId(),
                    deleteall,
                    context.getServiceCollection()));
        messages.add(Component.text().append(messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.reset"))
                .style(Style.style(TextDecoration.UNDERLINED))
                .clickEvent(SpongeComponents.executeCallback(cs -> {
                    this.callbacks.values().removeIf(x -> Instant.now().isAfter(x.until));
                    if (this.callbacks.containsKey(responsible)) {
                        final Delete delete = this.callbacks.get(responsible);
                        this.callbacks.remove(responsible);
                        if (Instant.now().isBefore(delete.until) && delete.all == deleteall && delete.user.equals(user.getUniqueId())) {
                            // execute that callback
                            delete.accept(targetAudience);
                        }
                    } else {
                        messageProvider.getMessageFor(targetAudience, "command.nucleus.reset.resetfailed");
                    }

                    this.callbacks.remove(responsible);
                })).build());

        targetAudience.sendMessage(Component.join(Component.newline(), messages));
        return context.successResult();
    }

    private static class Delete implements Consumer<Audience> {

        private final UUID user;
        private final boolean all;
        private final INucleusServiceCollection serviceCollection;
        private final Instant until;

        Delete(final Instant until, final UUID user, final boolean all, final INucleusServiceCollection serviceCollection) {
            this.user = user;
            this.all = all;
            this.serviceCollection = serviceCollection;
            this.until = until;
        }

        public UUID getUser() {
            return this.user;
        }

        public boolean isAll() {
            return this.all;
        }

        public Instant getUntil() {
            return this.until;
        }

        @Override
        public void accept(final Audience source) {
            final IMessageProviderService messageProvider = this.serviceCollection.messageProvider();
            final User user = Sponge.getServer().getUserManager().get(this.user).get();
            if (user.isOnline()) {
                final ServerPlayer player = user.getPlayer().get();
                final Component kickReason = messageProvider.getMessageFor(player, "command.kick.defaultreason");
                player.kick(kickReason);

                // Let Sponge do what it needs to close the user off.
                Task.builder().execute(() -> this.accept(source)).delay(Ticks.of(1)).plugin(this.serviceCollection.pluginContainer()).build();
                return;
            }

            source.sendMessage(messageProvider.getMessageFor(source, "command.nucleus.reset.starting", user.getName()));

            // Ban temporarily.
            final BanService bss = Sponge.getServer().getServiceProvider().banService();
            final boolean isBanned = bss.getBanFor(user.getProfile()).isPresent();
            bss.addBan(Ban.builder().type(BanTypes.PROFILE).expirationDate(Instant.now().plus(30, ChronoUnit.SECONDS)).profile(user.getProfile())
                    .build());

            // Unload the player in a second, just to let events fire.
            Sponge.getAsyncScheduler().createExecutor(this.serviceCollection.pluginContainer()).schedule(() -> {
                final IStorageService.Keyed<UUID, IUserQueryObject, IUserDataObject> userStorageService =
                        this.serviceCollection.storageManager().getUserService();

                // Get the file to delete.
                try {
                    // Remove them from the cache immediately.
                    userStorageService.clearCache();
                    userStorageService.delete(user.getUniqueId());
                    if (this.all) {
                        if (Sponge.getServer().getUserManager().delete(user)) {
                            source.sendMessage(messageProvider.getMessageFor(source, "command.nucleus.reset.completeall", user.getName()));
                        } else {
                            source.sendMessage(messageProvider.getMessageFor(source, "command.nucleus.reset.completenonm", user.getName()));
                        }
                    } else {
                        source.sendMessage(messageProvider.getMessageFor(source, "command.nucleus.reset.complete", user.getName()));
                    }

                    source.sendMessage(messageProvider.getMessageFor(source, "command.nucleus.reset.restartadvised", user.getName()));
                } catch (final Exception e) {
                    source.sendMessage(messageProvider.getMessageFor(source, "command.nucleus.reset.failed", user.getName()));
                } finally {
                    if (!isBanned) {
                        bss.getBanFor(user.getProfile()).ifPresent(bss::removeBan);
                    }
                }
            } , 1, TimeUnit.SECONDS);
        }
    }
}
