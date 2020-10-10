/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.scaffold.command.annotation.CommandModifier;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.storage.WorldProperties;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;

public interface ICommandContext {

    ServerPlayer reqiurePlayer() throws CommandException;

    CommandCause getCause();

    String getCommandKey();

    Object getCommandSourceRoot();

    Optional<UUID> getUniqueId();

    /**
     * Gets the current cooldown for the command
     *
     * @return
     */
    int getCooldown();

    void setCooldown(int cooldown);

    double getCost();

    void setCost(double cost);

    int getWarmup();

    /**
     * Sets the warmup in seconds.
     *
     * @param warmup The warmup in seconds
     */
    void setWarmup(int warmup);

    <T> T requireOne(Parameter.Value<T> key);

    ServerPlayer getPlayerFromArgs(String key, String errorKey) throws CommandException;

    default ServerPlayer getPlayerFromArgs() throws CommandException {
        return this.getPlayerFromArgs(NucleusParameters.Keys.PLAYER, "command.playeronly");
    }

    ServerPlayer getCommandSourceAsPlayerUnchecked();

    default User getUserFromArgs() throws CommandException {
        return this.getUserFromArgs(NucleusParameters.Keys.USER, "command.playeronly");
    }

    default User getUserFromArgs(String key) throws CommandException {
        return this.getUserFromArgs(key, "command.playeronly");
    }

    User getUserFromArgs(String key, String errorKey) throws CommandException;

    default User getUserFromArgs(final Parameter.Value<? extends User> key) throws CommandException {
        return this.getUserFromArgs(key, "command.playeronly");
    }

    User getUserFromArgs(Parameter.Value<? extends User> key, String errorKey) throws CommandException;

    User getUserFromArgs(Parameter.Key<? extends User> key, String errorKey) throws CommandException;

    boolean hasFlag(String name);

    boolean hasAny(Parameter.Key<?> name);

    default boolean hasAny(Parameter.Value<?> name) {
        return this.hasAny(name.getKey());
    }

    <T> Optional<T> getOne(Parameter.Key<T> name);

    <T> Optional<T> getOne(Parameter.Value<T> name);

    <T> Collection<? extends T> getAll(Parameter.Key<T> name);

    default <T> Optional<T> getOne(final String name, final Class<T> clazz) {
        return this.getOne(Parameter.key(name, TypeToken.of(clazz)));
    }

    default <T> Optional<T> getOne(final String name, final TypeToken<T> clazz) {
        return this.getOne(Parameter.key(name, clazz));
    }

    default <T> Collection<? extends T> getAll(final String name, final Class<T> clazz) {
        return this.getAll(Parameter.key(name, TypeToken.of(clazz)));
    }

    default <T> Collection<? extends T> getAll(final String name, final TypeToken<T> clazz) {
        return this.getAll(Parameter.key(name, clazz));
    }

    <T> Collection<? extends T> getAll(Parameter.Value<T> name);

    <T> T requireOne(Parameter.Key<T> key);

    @NonNull
    default <T> T requireOne(final String name, final Class<T> clazz) {
        return this.requireOne(Parameter.key(name, TypeToken.of(clazz)));
    }

    @NonNull
    default <T> T requireOne(final String name, final TypeToken<T> clazz) {
        return this.requireOne(Parameter.key(name, clazz));
    }

    INucleusServiceCollection getServiceCollection();

    ICommandResult successResult();

    ICommandResult failResult();

    ICommandResult errorResultLiteral(Component message);

    ICommandResult errorResult(String key, Object... args);

    ICommandResult getResultFromBoolean(boolean success);

    CommandException createException(Throwable ex, String key, Object... args);

    CommandException createException(String key, Object... args);

    @NonNull
    default ServerPlayer getIfPlayer() throws CommandException {
        return this.getIfPlayer("command.playeronly");
    }

    @NonNull
    ServerPlayer getIfPlayer(String errorKey) throws CommandException;

    Map<CommandModifier, ICommandModifier> modifiers();

    Collection<Consumer<ICommandContext>> failActions();

    boolean testPermission(String permission);

    boolean testPermissionFor(Subject subject, String permission);

    String getMessageString(String key, Object... replacements);

    String getMessageStringFor(Audience to, String key, Object... replacements);

    Component getMessage(String key, Object... replacements);

    Component getMessageFor(Audience to, String key, Object... replacements);

    default String getTimeString(final Duration duration) {
        return this.getTimeString(duration.getSeconds());
    }

    String getTimeString(long seconds);

    /**
     * Sends a message to the command invoker.
     *
     * @param key The translation key
     * @param replacements The replacements
     */
    void sendMessage(String key, Object... replacements);

    void sendMessageText(Component message);

    void sendMessageTo(Audience to, String key, Object... replacements);

    boolean is(Object other);

    boolean is(Class<?> other);

    boolean is(User x);

    default boolean isNot(final User x) {
        return !this.is(x);
    }

    boolean isUser();

    boolean isConsoleAndBypass();

    WorldProperties getWorldPropertiesOrFromSelf(Parameter.Key<WorldProperties> worldKey) throws CommandException;

    Optional<WorldProperties> getWorldPropertiesOrFromSelfOptional(Parameter.Key<WorldProperties> worldKey);

    String getName();

    Component getDisplayName();

    Component getDisplayName(UUID uuid);

    default String getTimeToNowString(final Instant endTime) {
        return this.getTimeString(Duration.between(Instant.now(), endTime).abs());
    }

    default OptionalInt getLevel(final String key) {
        return this.getLevelFor(this.getCause(), key);
    }

    OptionalInt getLevelFor(Subject subject, String key);

    default int getLevel(final String key, final String permissionIfNoLevel) {
        return this.getLevelFor(this.getCause(), key, permissionIfNoLevel);
    }

    default int getLevelFor(final Subject subject, final String key, final String permissionIfNoLevel) {
        return this.getLevelFor(subject, key).orElseGet(() -> this.testPermissionFor(subject, permissionIfNoLevel) ? 1 : 0);
    }

    /**
     * Gets whether the permission level is okay.
     *
     * @param actee The person that is targetted
     * @param key The level key
     * @param permissionIfNoLevel The permission to check if no level is provided
     * @param isSameLevel If true, this returns true if the actor and actee have the same permission level, if false,
     *                      returns false in the same situation.
     * @return if the level is okay to proceed
     */
    boolean isPermissionLevelOkay(Subject actee, String key, String permissionIfNoLevel, boolean isSameLevel);

    void removeModifier(String modifierId);

    void removeModifier(ICommandModifier modifier);

    void addFailAction(Consumer<ICommandContext> action);

    Audience getAudience();

    Locale getLocale();
}
