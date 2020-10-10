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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Nameable;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;

public final class CommandContextImpl implements ICommandContext {

    private final INucleusServiceCollection serviceCollection;
    private final String commandkey;
    private final String stringName;
    private double cost;
    private int cooldown;
    private int warmup;
    private final boolean isBypass;
    private final CommandCause cause;
    private final CommandContext context;
    private final Map<CommandModifier, ICommandModifier> modifiers;
    private final ArrayList<Consumer<ICommandContext>> failActions = new ArrayList<>();

    private static String getFriendlyName(final CommandCause cause) {
        final Object root = cause.root();
        if (root instanceof Nameable) {
            return ((Nameable) root).getName();
        }
        return "Server";
    }

    public CommandContextImpl(
            final CommandCause cause,
            final CommandContext context,
            final INucleusServiceCollection serviceCollection,
            final CommandControl control,
            final boolean isBypass,
            final Map<CommandModifier, ICommandModifier> modifiers) {
        this.cause = cause;
        this.commandkey = control.getCommandKey();
        this.context = context;
        this.serviceCollection = serviceCollection;
        this.cost = control.getCost(cause);
        this.cooldown = control.getCooldown(cause);
        this.warmup = control.getWarmup(cause);
        this.modifiers = new HashMap<>(modifiers);
        this.stringName = CommandContextImpl.getFriendlyName(cause);
        this.isBypass = isBypass;
    }

    @Override
    public ServerPlayer reqiurePlayer() throws CommandException {
        final Object root = this.cause.root();
        if (root instanceof ServerPlayer) {
            return (ServerPlayer) root;
        }
        throw new CommandException(Component.text("This command must be executed by a player!", NamedTextColor.RED));
    }

    @Override
    public CommandCause getCause() {
        return this.cause;
    }

    @Override
    public String getCommandKey() {
        return this.commandkey;
    }

    @Override
    public boolean hasFlag(final String name) {
        return this.context.hasFlag(name);
    }

    @Override
    public boolean hasAny(final Parameter.Key<?> name) {
        return this.context.hasAny(name);
    }

    @Override
    public Object getCommandSourceRoot() {
        return this.cause.root();
    }

    @Override
    public <T> Optional<T> getOne(final Parameter.Key<T> name) {
        return this.context.getOne(name);
    }

    @Override
    public <T> Optional<T> getOne(final Parameter.Value<T> name) {
        return this.context.getOne(name);
    }

    @Override
    public <T> Collection<? extends T> getAll(final Parameter.Key<T> name) {
        return this.context.getAll(name);
    }

    @Override
    public <T> Collection<? extends T> getAll(final Parameter.Value<T> name) {
        return this.context.getAll(name);
    }

    @Override
    public <T> T requireOne(final Parameter.Key<T> key) {
        return this.context.requireOne(key);
    }

    @Override
    public <T> T requireOne(final Parameter.Value<T> key) {
        return this.context.requireOne(key);
    }

    @Override
    public ServerPlayer getPlayerFromArgs(final String key, final String errorKey) throws CommandException {
        final Optional<? extends ServerPlayer> player = this.getOne(key, ServerPlayer.class);
        if (player.isPresent()) {
            return player.get();
        } else {
            return this.getIfPlayer(errorKey);
        }
    }

    @Override
    public ServerPlayer getCommandSourceAsPlayerUnchecked() {
        try {
            return this.reqiurePlayer();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserFromArgs(final String key, final String errorKey) throws CommandException {
        return this.getUserFromArgs(Parameter.key(key, TypeToken.of(User.class)), errorKey);
    }

    @Override
    public User getUserFromArgs(final Parameter.Value<? extends User> key, final String errorKey) throws CommandException {
        return this.getUserFromArgs(key.getKey(), errorKey);
    }

    @Override
    public User getUserFromArgs(final Parameter.Key<? extends User> key, final String errorKey) throws CommandException {
        final Optional<? extends User> user = this.context.getOne(key);
        if (user.isPresent()) {
            return user.get();
        } else {
            return this.getIfPlayer(errorKey).getUser();
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

    public ICommandResult errorResultLiteral(final Component message) {
        return new CommandResultImpl.Literal(message);
    }

    @Override
    public ICommandResult errorResult(final String key, final Object... args) {
        return new CommandResultImpl(key, args);
    }

    @Override
    public CommandException createException(final Throwable th, final String key, final Object... args) {
        final Audience source = this.cause.getAudience();
        return new CommandException(
                this.serviceCollection.messageProvider().getMessageFor(source, key, args),
                th
        );
    }

    @Override
    public CommandException createException(final String key, final Object... args) {
        final Audience source = this.cause.getAudience();
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

    @Override public Collection<Consumer<ICommandContext>> failActions() {
        return ImmutableList.copyOf(this.failActions);
    }

    @Override public void addFailAction(final Consumer<ICommandContext> action) {
        this.failActions.add(action);
    }

    @Override public Audience getAudience() {
        return this.cause.getAudience();
    }

    @Override
    public Locale getLocale() {
        if (this.cause.getAudience() instanceof ServerPlayer) {
            return ((ServerPlayer) this.cause.getAudience()).getLocale();
        }
        return null;
    }

    @Override public int getWarmup() {
        return this.warmup;
    }

    @Override public void setWarmup(final int warmup) {
        this.warmup = warmup;
    }

    @Override public boolean testPermission(final String permission) {
        return this.testPermissionFor(this.cause.getSubject(), permission);
    }

    @Override public boolean testPermissionFor(final Subject subject, final String permission) {
        return this.serviceCollection.permissionService().hasPermission(subject, permission);
    }

    @Override public String getMessageString(final String key, final Object... replacements) {
        return this.getMessageStringFor(this.cause.getAudience(), key, replacements);
    }

    @Override public String getMessageStringFor(final Audience to, final String key, final Object... replacements) {
        final Audience audience = this.cause.getAudience();
        final Locale locale;
        if (audience instanceof LocaleSource) {
            locale = ((LocaleSource) audience).getLocale();
        } else {
            locale = Sponge.getServer().getLocale();
        }
        return this.serviceCollection.messageProvider().getMessageString(locale, key, replacements);
    }

    @Override
    public Component getMessageFor(final Audience to, final String key, final Object... replacements) {
        return this.serviceCollection.messageProvider().getMessageFor(to, key, replacements);
    }

    @Override
    public Component getMessage(final String key, final Object... replacements) {
        return this.getMessageFor(this.cause.getAudience(), key, replacements);
    }

    @Override
    public String getTimeString(final long seconds) {
        return this.serviceCollection.messageProvider().getTimeString(this.getLocaleFromAudience(this.cause.getAudience()), seconds);
    }

    @Override
    public void sendMessage(final String key, final Object... replacements) {
        this.sendMessageTo(this.cause.getAudience(), key, replacements);
    }

    @Override
    public void sendMessageText(final Component message) {
        this.context.sendMessage(message);
    }

    @Override public void sendMessageTo(final Audience source, final String key, final Object... replacements) {
        this.serviceCollection.messageProvider().sendMessageTo(source, key, replacements);
    }

    @Override public boolean is(final Object other) {
        return this.getCommandSourceRoot().equals(other);
    }

    @Override public boolean is(final Class<?> other) {
        return other.isInstance(this.getCommandSourceRoot());
    }

    @Override
    public boolean isConsoleAndBypass() {
        return this.isBypass && this.getCommandSourceRoot() instanceof SystemSubject;
    }

    @Override
    public WorldProperties getWorldPropertiesOrFromSelf(final Parameter.Key<WorldProperties> worldKey) throws CommandException {
        return this.getWorldPropertiesOrFromSelfOptional(worldKey).orElseThrow(() -> new CommandException(this.getMessage("command.specifyworld")));
    }

    @Override
    public Optional<WorldProperties> getWorldPropertiesOrFromSelfOptional(final Parameter.Key<WorldProperties> worldKey) {
        final Optional<WorldProperties> optionalWorldProperties = this.context.getOne(worldKey);
        if (!optionalWorldProperties.isPresent()) {
            return this.cause.getLocation().map(x -> x.getWorld().getProperties());
        }

        return Optional.empty();
    }

    @Override
    public Component getDisplayName() {
        return this.getServiceCollection().playerDisplayNameService().getDisplayName(this.cause.getAudience());
    }

    @Override
    public Component getDisplayName(final UUID uuid) {
        return this.getServiceCollection().playerDisplayNameService().getDisplayName(uuid);
    }

    @Override
    public String getName() {
        return this.stringName;
    }

    @Override
    public OptionalInt getLevelFor(final Subject subject, final String key) {
        return this.serviceCollection.permissionService().getIntOptionFromSubject(subject, key);
    }

    @Override
    public boolean isPermissionLevelOkay(final Subject actee, final String key, final String permissionIfNoLevel, final boolean isSameLevel) {
        return this.serviceCollection.permissionService().isPermissionLevelOkay(this.cause.getSubject(), actee, key, permissionIfNoLevel, isSameLevel);
    }

    @Override
    public Optional<UUID> getUniqueId() {
        return Optional.empty();
    }

    @Override
    public @NonNull ServerPlayer getIfPlayer(final String errorKey) throws CommandException {
        if (this.getCommandSourceRoot() instanceof ServerPlayer) {
            return (ServerPlayer) this.getCommandSourceRoot();
        }

        throw new CommandException(
                this.getServiceCollection().messageProvider().getMessageFor(this.cause.getAudience(), errorKey)
        );
    }

    @Override
    public boolean is(final User x) {
        final Object source = this.getCommandSourceRoot();
        if (source instanceof ServerPlayer) {
            return ((ServerPlayer) source).getUniqueId().equals(x.getUniqueId());
        }

        return false;
    }

    @Override
    public boolean isUser() {
        return this.getCommandSourceRoot() instanceof User;
    }

    private Locale getLocaleFromAudience(final Audience audience) {
        final Locale locale;
        if (audience instanceof LocaleSource) {
            locale = ((LocaleSource) audience).getLocale();
        } else {
            locale = Sponge.getServer().getLocale();
        }
        return locale;
    }

}
