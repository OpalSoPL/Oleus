/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandResult;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.storage.util.ThrownSupplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public abstract class CommandContextImpl<P extends CommandSource> implements ICommandContext.Mutable<P> {

    private final INucleusServiceCollection serviceCollection;
    private final String commandkey;
    private final String stringName;
    private double cost = 0;
    private int cooldown = 0;
    private int warmup = 0;
    private final Cause cause;
    final CommandContext context;
    private final ThrownSupplier<P, CommandException> source;
    private final Map<CommandModifier, ICommandModifier> modifiers;
    private final ArrayList<Consumer<ICommandContext<P>>> failActions = new ArrayList<>();

    CommandContextImpl(final Cause cause,
            final CommandContext context,
            final INucleusServiceCollection serviceCollection,
            final ThrownSupplier<P, CommandException> source,
            final P sourceDirect,
            final CommandControl control,
            final Map<CommandModifier, ICommandModifier> modifiers) {
        this.cause = cause;
        this.commandkey = control.getCommandKey();
        this.context = context;
        this.source = source;
        this.serviceCollection = serviceCollection;
        this.cost = control.getCost(sourceDirect);
        this.cooldown = control.getCooldown(sourceDirect);
        this.warmup = control.getWarmup(sourceDirect);
        this.modifiers = new HashMap<>(modifiers);
        this.stringName = sourceDirect.getName();
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public String getCommandKey() {
        return this.commandkey;
    }

    @Override
    public P getCommandSource() throws CommandException {
        return this.source.get();
    }

    @Override
    public <T> Optional<T> getOne(final String name, final Class<T> clazz) {
        return this.context.getOne(name);
    }

    @Override
    public boolean hasAny(final String name) {
        return this.context.hasAny(name);
    }

    @Override
    public <T> Collection<T> getAll(final String name, final Class<T> clazz) {
        return this.context.getAll(name);
    }

    @Override public <T> Optional<T> getOne(final String name, final TypeToken<T> clazz) {
        return this.context.getOne(name);
    }

    @Override public <T> Collection<T> getAll(final String name, final TypeToken<T> clazz) {
        return this.context.getAll(name);
    }

    @Override public <T> @NonNull T requireOne(final String name, final TypeToken<T> clazz) {
        return this.context.requireOne(name);
    }

    @Override
    public <T> @NonNull T requireOne(final String name, final Class<T> clazz) {
        return this.context.requireOne(name);
    }

    @Override
    public Player getPlayerFromArgs(final String key, final String errorKey) throws CommandException {
        final Optional<Player> player = this.getOne(key, Player.class);
        if (player.isPresent()) {
            return player.get();
        } else {
            return this.getIfPlayer(errorKey);
        }
    }

    @Override
    public P getCommandSourceUnchecked() {
        try {
            return this.source.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Player getCommandSourceAsPlayerUnchecked() {
        try {
            return (Player) this.source.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserFromArgs(final String key, final String errorKey) throws CommandException {
        final Optional<User> player = this.getOne(key, User.class);
        if (player.isPresent()) {
            return player.get();
        } else {
            return this.getIfPlayer(errorKey);
        }
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public void setCooldown(final int cooldown) {
        this.cooldown = Math.max(cooldown, 0);
    }

    @Override
    public double getCost() {
        return this.cost;
    }

    @Override
    public void setCost(final double cost) {
        this.cost = Math.max(cost, 0);
    }

    @Override
    public <T> void put(final String name, final Class<T> clazz, final T obj) {
        this.context.putArg(name, obj);
    }

    @Override
    public <T> void putAll(final String name, final Class<T> clazz, final Collection<? extends T> obj) {
        for (final T o : obj) {
            this.context.putArg(name, o);
        }
    }

    @Override
    public ICommandResult successResult() {
        return CommandResultImpl.SUCCESS;
    }

    @Override
    public ICommandResult failResult() {
        return CommandResultImpl.FAILURE;
    }

    @Override public ICommandResult getResultFromBoolean(final boolean success) {
        if (success) {
            return this.successResult();
        }
        return this.failResult();
    }

    public ICommandResult errorResultLiteral(final Text message) {
        return new CommandResultImpl.Literal(message);
    }

    @Override
    public ICommandResult errorResult(final String key, final Object... args) {
        return new CommandResultImpl(this.serviceCollection.messageProvider(), key, args);
    }

    @Override
    public CommandException createException(final Throwable th, final String key, final Object... args) {
        final Optional<? extends CommandSource> c = this.source.asOptional();
        final CommandSource source;
        if (c.isPresent()) {
            source = c.get();
        } else {
            source = Sponge.getServer().getConsole();
        }

        return new CommandException(
                this.serviceCollection.messageProvider().getMessageFor(source, key, args),
                th
        );
    }

    @Override
    public CommandException createException(final String key, final Object... args) {
        final Optional<? extends CommandSource> c = this.source.asOptional();
        final CommandSource source;
        if (c.isPresent()) {
            source = c.get();
        } else {
            source = Sponge.getServer().getConsole();
        }

        return new CommandException(
                this.serviceCollection.messageProvider().getMessageFor(source, key, args)
        );
    }

    @Override
    public INucleusServiceCollection getServiceCollection() {
        return this.serviceCollection;
    }

    @Override public Map<CommandModifier, ICommandModifier> modifiers() {
        return ImmutableMap.copyOf(this.modifiers);
    }

    @Override public void removeModifier(final String modifierId) {
        this.modifiers.entrySet().removeIf(x -> x.getKey().value().equals(modifierId));
    }

    @Override
    public void removeModifier(final ICommandModifier modifier) {
        this.modifiers.entrySet().removeIf(x -> x.getValue() == modifier);
    }

    @Override public Collection<Consumer<ICommandContext<P>>> failActions() {
        return ImmutableList.copyOf(this.failActions);
    }

    @Override public void addFailAction(final Consumer<ICommandContext<P>> action) {
        this.failActions.add(action);
    }

    @Override public int getWarmup() {
        return this.warmup;
    }

    @Override public void setWarmup(final int warmup) {
        this.warmup = warmup;
    }

    @Override public boolean testPermission(final String permission) {
        return this.testPermissionFor(this.getCommandSourceUnchecked(), permission);
    }

    @Override public boolean testPermissionFor(final Subject subject, final String permission) {
        return this.serviceCollection.permissionService().hasPermission(subject, permission);
    }

    @Override public String getMessageString(final String key, final Object... replacements) {
        return getMessageStringFor(this.getCommandSourceUnchecked(), key, replacements);
    }

    @Override public String getMessageStringFor(final CommandSource to, final String key, final Object... replacements) {
        return this.serviceCollection.messageProvider().getMessageString(to.getLocale(), key, replacements);
    }

    @Override public Text getMessageFor(final CommandSource to, final String key, final Object... replacements) {
        return this.serviceCollection.messageProvider().getMessageFor(this.getCommandSourceUnchecked(), key, replacements);
    }

    @Override public Text getMessage(final String key, final Object... replacements) {
        return getMessageFor(this.getCommandSourceUnchecked(), key, replacements);
    }

    @Override public String getTimeString(final long seconds) {
        return this.serviceCollection.messageProvider().getTimeString(this.getCommandSourceUnchecked().getLocale(), seconds);
    }

    @Override public void sendMessage(final String key, final Object... replacements) {
        sendMessageTo(this.getCommandSourceUnchecked(), key, replacements);
    }

    @Override public void sendMessageText(final Text message) {
        this.getCommandSourceUnchecked().sendMessage(message);
    }

    @Override public void sendMessageTo(final MessageReceiver source, final String key, final Object... replacements) {
        this.serviceCollection.messageProvider().sendMessageTo(source, key, replacements);
    }

    @Override public boolean is(final CommandSource other) {
        return this.source.getUnchecked().equals(other);
    }

    @Override public boolean is(final Class<?> other) {
        return other.isInstance(this.source.getUnchecked());
    }

    @Override public boolean isConsoleAndBypass() {
        return false;
    }

    @Override public boolean is(final User x) {
        return false;
    }

    @Override public Optional<WorldProperties> getWorldPropertiesOrFromSelf(final String worldKey) {
        final Optional<WorldProperties> optionalWorldProperties = this.context.getOne(worldKey);
        if (!optionalWorldProperties.isPresent()) {
            final CommandSource source = this.getCommandSourceUnchecked();
            if (source instanceof Locatable) {
                return Optional.of(((Locatable) source).getWorld().getProperties());
            }
        }

        return Optional.empty();
    }

    @Override public Text getDisplayName() {
        return this.getServiceCollection().playerDisplayNameService().getDisplayName(this.getCommandSourceUnchecked());
    }

    @Override public Text getDisplayName(final UUID uuid) {
        return this.getServiceCollection().playerDisplayNameService().getDisplayName(uuid);
    }

    @Override public String getName() {
        return this.stringName;
    }

    @Override public OptionalInt getLevelFor(final Subject subject, final String key) {
        return this.serviceCollection.permissionService().getIntOptionFromSubject(subject, key);
    }

    @Override public boolean isPermissionLevelOkay(final Subject actee, final String key, final String permissionIfNoLevel, final boolean isSameLevel) {
        return this.serviceCollection.permissionService().isPermissionLevelOkay(this.getCommandSourceUnchecked(), actee, key, permissionIfNoLevel, isSameLevel);
    }

    public static class Any extends CommandContextImpl<CommandSource> {

        public Any(final Cause cause,
                final CommandContext context,
                final INucleusServiceCollection serviceCollection,
                final CommandSource target,
                final CommandControl control,
                final Map<CommandModifier, ICommandModifier> modifiers) throws CommandException {
            super(cause, context, serviceCollection, () -> target, target, control, modifiers);
        }

        @Override public Optional<UUID> getUniqueId() {
            return Optional.empty();
        }

        @Override
        public @NonNull Player getIfPlayer(final String errorKey) throws CommandException {
            if (this.getCommandSource() instanceof Player) {
                return (Player) this.getCommandSource();
            }

            throw new CommandException(
                    this.getServiceCollection().messageProvider().getMessageFor(this.getCommandSource(), errorKey)
            );
        }

        @Override
        public boolean is(final User x) {
            try {
                final CommandSource source = this.getCommandSource();
                if (source instanceof Player) {
                    return ((Player) source).getUniqueId().equals(x.getUniqueId());
                }

                return false;
            } catch (final CommandException e) {
                return false;
            }
        }

        @Override public boolean isUser() {
            return this.getCommandSourceUnchecked() instanceof User;
        }
    }


    public static class Console extends CommandContextImpl<ConsoleSource> {

        private final boolean isBypass;

        public Console(final Cause cause,
                final CommandContext context,
                final INucleusServiceCollection serviceCollection,
                final ConsoleSource target,
                final CommandControl control,
                final Map<CommandModifier, ICommandModifier> modifiers,
                final boolean isBypass) throws CommandException {
            super(cause, context, serviceCollection, () -> target, target, control, modifiers);
            this.isBypass = isBypass;
        }

        @Override public Optional<UUID> getUniqueId() {
            return Optional.empty();
        }

        @Override
        public @NonNull Player getIfPlayer(final String errorKey) throws CommandException {
            throw new CommandException(
                    this.getServiceCollection().messageProvider().getMessageFor(this.getCommandSource(), errorKey)
            );
        }

        @Override public boolean is(final Class<?> other) {
            return ConsoleSource.class.isAssignableFrom(other);
        }

        @Override public boolean isUser() {
            return false;
        }

        @Override public boolean isConsoleAndBypass() {
            return this.isBypass;
        }

        @Override public Optional<WorldProperties> getWorldPropertiesOrFromSelf(final String worldKey) {
            return this.context.getOne(worldKey);
        }
    }

    public static class PlayerSource extends CommandContextImpl<Player> {

        private final UUID uuid;

        public PlayerSource(final Cause cause,
                final CommandContext context,
                final INucleusServiceCollection serviceCollection,
                final ThrownSupplier<Player, CommandException> source,
                final Player player,
                final CommandControl control,
                final Map<CommandModifier, ICommandModifier> modifiers) throws CommandException {
            super(cause, context, serviceCollection, source, player, control, modifiers);
            this.uuid = source.asOptional().map(Identifiable::getUniqueId).get();
        }

        @Override
        public boolean is(final Class<?> other) {
            return Player.class.isAssignableFrom(other);
        }

        @Override
        public boolean is(final User x) {
            return x.getUniqueId().equals(this.uuid);
        }

        @Override public boolean isUser() {
            return true;
        }

        @Override
        public Optional<WorldProperties> getWorldPropertiesOrFromSelf(final String worldKey) {
            final Optional<WorldProperties> worldProperties = this.context.getOne(worldKey);
            if (!worldProperties.isPresent()) {
                return Optional.of(this.getCommandSourceAsPlayerUnchecked().getWorld().getProperties());
            }

            return Optional.empty();
        }

        @Override
        public Optional<UUID> getUniqueId() {
            return Optional.of(this.uuid);
        }

        @Override
        public @NonNull Player getIfPlayer(final String errorKey) throws CommandException {
            return this.getCommandSource();
        }
    }

}
