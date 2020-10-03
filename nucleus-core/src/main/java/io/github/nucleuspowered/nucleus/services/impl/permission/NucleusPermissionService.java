/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.permission;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.api.util.NoExceptionAutoClosable;
import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.services.interfaces.IPermissionService;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import io.github.nucleuspowered.nucleus.services.interfaces.annotation.PermissionMetadata;
import io.github.nucleuspowered.nucleus.services.interfaces.data.SuggestedLevel;
import io.github.nucleuspowered.nucleus.util.PermissionMessageChannel;
import io.github.nucleuspowered.nucleus.util.PrettyPrinter;
import io.github.nucleuspowered.nucleus.util.ThrownFunction;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Level;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.SystemSubject;
import org.spongepowered.api.block.entity.CommandBlock;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.util.Tristate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

@Singleton
public class NucleusPermissionService implements IPermissionService, IReloadableService.Reloadable, ContextCalculator<Subject> {

    private final IMessageProviderService messageProviderService;
    private final INucleusServiceCollection serviceCollection;
    private boolean init = false;
    private boolean useRole = false;
    private boolean consoleOverride = false;
    private final Set<String> failedChecks = new HashSet<>();
    private final Map<String, IPermissionService.Metadata> metadataMap = new HashMap<>();
    private final Map<String, IPermissionService.Metadata> prefixMetadataMap = new HashMap<>();

    private final Map<UUID, Map<String, Context>> standardContexts = new ConcurrentHashMap<>();
    private final Map<SuggestedLevel, Set<SubjectReference>> appliedRoles = new HashMap<>();

    @Inject
    public NucleusPermissionService(
            final INucleusServiceCollection serviceCollection,
            final IReloadableService service) {
        this.messageProviderService = serviceCollection.messageProvider();
        this.serviceCollection = serviceCollection;
        service.registerReloadable(this);
    }

    @Override
    public void assignUserRoleToDefault() {
        this.assignRoleToGroup(SuggestedLevel.USER, Sponge.getServer().getServiceProvider().permissionService().getDefaults());
    }

    @Override
    public void assignRoleToGroup(final SuggestedLevel role, final Subject subject) {
        for (final Map.Entry<String, IPermissionService.Metadata> permission : this.metadataMap.entrySet()) {
            if (permission.getValue().getSuggestedLevel() == role) {
                subject.getTransientSubjectData().setPermission(ImmutableSet.of(), permission.getValue().getPermission(), Tristate.TRUE);
            }
        }
        for (final Map.Entry<String, IPermissionService.Metadata> permission : this.prefixMetadataMap.entrySet()) {
            if (permission.getValue().getSuggestedLevel() == role) {
                subject.getTransientSubjectData().setPermission(ImmutableSet.of(), permission.getValue().getPermission(), Tristate.TRUE);
            }
        }
    }

    @Override
    public void registerContextCalculator(final ContextCalculator<Subject> calculator) {
        Sponge.getServer().getServiceProvider().permissionService().registerContextCalculator(calculator);
    }

    @Override
    public boolean hasPermission(final UUID playerUUID, final String permission) {
        return this.hasPermission(Sponge.getServer().getUserManager().get(playerUUID)
                .orElseThrow(() -> new IllegalArgumentException("The UUID " + playerUUID + " is not a valid player UUID")), permission);
    }

    @Override
    public boolean hasPermission(final Subject permissionSubject, final String permission) {
        return this.hasPermission(permissionSubject, permission, this.useRole);
    }

    @Override
    public Tristate hasPermissionTristate(final Subject subject, final String permission) {
        return this.hasPermissionTristate(subject, permission, this.useRole);
    }

    @Override
    public boolean hasPermissionWithConsoleOverride(final Subject subject, final String permission, final boolean permissionIfConsoleAndOverridden) {
        if (this.consoleOverride && subject instanceof SystemSubject) {
            return permissionIfConsoleAndOverridden;
        }

        return this.hasPermission(subject, permission);
    }

    @Override public boolean isConsoleOverride(final Subject subject) {
        return this.consoleOverride && subject instanceof SystemSubject;
    }

    @Override public void onReload(final INucleusServiceCollection serviceCollection) {
        final CoreConfig coreConfig = serviceCollection.configProvider().getModuleConfig(CoreConfig.class);
        this.useRole = coreConfig.isUseParentPerms();
        this.consoleOverride = coreConfig.isConsoleOverride();
    }

    @Override public void registerDescriptions() {
        Preconditions.checkState(!this.init);
        this.init = true;
        final PermissionService ps = Sponge.getServer().getServiceProvider().permissionService();
        for (final Map.Entry<String, IPermissionService.Metadata> entry : this.metadataMap.entrySet()) {
            final SuggestedLevel level = entry.getValue().getSuggestedLevel();
            if (level.getRole() != null) {
                ps.newDescriptionBuilder(this.serviceCollection.pluginContainer())
                        .assign(level.getRole(), true)
                        .description(Component.text(entry.getValue().getDescription(this.messageProviderService)))
                        .id(entry.getKey()).register();
            }
        }
    }

    @Override public void register(final String permission, final PermissionMetadata metadata, final String moduleid) {
        final NucleusPermissionService.Metadata m = new NucleusPermissionService.Metadata(permission, metadata, moduleid);
        if (metadata.isPrefix()) {
            this.prefixMetadataMap.put(permission.toLowerCase(), m);
        } else {
            this.metadataMap.put(permission.toLowerCase(), m);
        }
    }

    @Override public OptionalDouble getDoubleOptionFromSubject(final Subject player, final String... options) {
        return this.getTypedObjectFromSubject(
                string -> OptionalDouble.of(Double.parseDouble(string)),
                OptionalDouble.empty(),
                player,
                options);
    }

    @Override public OptionalLong getPositiveLongOptionFromSubject(final Subject player, final String... options) {
        return this.getTypedObjectFromSubject(
                string -> OptionalLong.of(Long.parseLong(string)),
                OptionalLong.empty(),
                player,
                options);
    }

    @Override public OptionalInt getPositiveIntOptionFromSubject(final Subject player, final String... options) {
        return this.getTypedObjectFromSubject(
                string -> OptionalInt.of(Integer.parseUnsignedInt(string)),
                OptionalInt.empty(),
                player,
                options);
    }

    @Override public OptionalInt getIntOptionFromSubject(final Subject player, final String... options) {
        return this.getTypedObjectFromSubject(
                string -> OptionalInt.of(Integer.parseInt(string)),
                OptionalInt.empty(),
                player,
                options);
    }

    private <T> T getTypedObjectFromSubject(final ThrownFunction<String, T, Exception> conversion, final T empty, final Subject player, final String... options) {
        try {
            final Optional<String> optional = this.getOptionFromSubject(player, options);
            if (optional.isPresent()) {
                return conversion.apply(optional.get());
            }
        } catch (final Exception e) {
            // ignored
        }

        return empty;
    }

    @Override public Optional<String> getOptionFromSubject(final Subject player, final String... options) {
        for (final String option : options) {
            final String o = option.toLowerCase();

            // Option for context.
            Optional<String> os = player.getOption(player.getActiveContexts(), o);
            if (os.isPresent()) {
                return os.map(r -> r.isEmpty() ? null : r);
            }

            // General option
            os = player.getOption(o);
            if (os.isPresent()) {
                return os.map(r -> r.isEmpty() ? null : r);
            }
        }

        return Optional.empty();
    }

    @Override public PermissionMessageChannel permissionMessageChannel(final String permission) {
        return new PermissionMessageChannel(this, permission);
    }

    @Override public List<IPermissionService.Metadata> getAllMetadata() {
        return ImmutableList.copyOf(this.metadataMap.values());
    }

    private boolean hasPermission(final Subject subject, final String permission, final boolean checkRole) {
        final Tristate tristate = this.hasPermissionTristate(subject, permission, checkRole);
        if (tristate == Tristate.UNDEFINED) {
            return subject.hasPermission(permission); // guarantees the correct response.
        }

        return tristate.asBoolean();
    }

    private Tristate hasPermissionTristate(final Subject subject, final String permission, final boolean checkRole) {
        if (checkRole && permission.startsWith("nucleus.")) {
            final Tristate tristate = subject.getPermissionValue(subject.getActiveContexts(), permission);
            if (tristate == Tristate.UNDEFINED) {
                @Nullable final IPermissionService.Metadata result = this.metadataMap.get(permission);
                if (result != null) { // check the "parent" perm
                    final String perm = result.getSuggestedLevel().getPermission();
                    if (perm == null) {
                        return subject.getPermissionValue(subject.getActiveContexts(), permission);
                    } else {
                        return subject.getPermissionValue(subject.getActiveContexts(), perm);
                    }
                }

                for (final Map.Entry<String, IPermissionService.Metadata> entry : this.prefixMetadataMap.entrySet()) {
                    if (permission.startsWith(entry.getKey())) {
                        final String perm = entry.getValue().getSuggestedLevel().getPermission();
                        if (perm == null) {
                            return subject.getPermissionValue(subject.getActiveContexts(), permission);
                        } else {
                            return subject.getPermissionValue(subject.getActiveContexts(), perm);
                        }
                    }
                }

                // if we get here, no registered permissions were found
                // therefore, warn
                if (this.failedChecks.add(permission)) {
                    final PrettyPrinter printer = new PrettyPrinter(80);
                    printer.add("Nucleus Permission Not Registered").centre().hr();
                    printer.add("Nucleus has not registered a permission properly. This is an error in Nucleus - please report to the Nucleus "
                            + "github.");
                    printer.hr();
                    printer.add("Permission: %s", permission);
                    printer.log(this.serviceCollection.logger(), Level.WARN);
                }

                // guarantees that the subject default is selected.
                return Tristate.UNDEFINED; // subject.hasPermission(permission);
            }

            return tristate;
        }

        return Tristate.UNDEFINED;
    }

    @Override
    public Optional<IPermissionService.Metadata> getMetadataFor(final String permission) {
        return Optional.ofNullable(this.metadataMap.get(permission));
    }

    @Override
    public boolean isPermissionLevelOkay(final Subject actor, final Subject actee, final String key, final String permission, final boolean isSameOkay) {
        final int actorLevel =
                this.getDeclaredLevel(actor, key).orElseGet(() -> this.hasPermission(actor, permission) ? this.getDefaultLevel(actor) : 0);
        final int acteeLevel =
                this.getDeclaredLevel(actee, key).orElseGet(() -> this.hasPermission(actee, permission) ? this.getDefaultLevel(actee) : 0);
        if (isSameOkay) {
            return actorLevel >= acteeLevel;
        } else {
            return actorLevel > acteeLevel;
        }
    }

    @Override
    public void setContext(final Subject subject, final Context context) {
        if (subject instanceof Identifiable) {
            this.setContext(((Identifiable) subject).getUniqueId(), context);
        }
    }

    private void setContext(final UUID uuid, final Context context) {
        this.standardContexts.computeIfAbsent(uuid, k -> new HashMap<>()).put(context.getKey().toLowerCase(), context);
    }

    @Override
    public NoExceptionAutoClosable setContextTemporarily(final Subject subject, final Context context) {
        if (subject instanceof Identifiable) {
            final UUID uuid = ((Identifiable) subject).getUniqueId();
            final Context old = this.standardContexts.computeIfAbsent(uuid, k -> new HashMap<>()).put(context.getKey().toLowerCase(), context);
            return () -> {
                this.removeContext(uuid, context.getKey().toLowerCase());
                if (old != null) {
                    this.setContext(uuid, context);
                }
            };
        }
        return NoExceptionAutoClosable.EMPTY;
    }

    @Override
    public void removeContext(final UUID subject, final String key) {
        final Map<String, Context> contexts = this.standardContexts.get(subject);
        if (contexts != null && !contexts.isEmpty()) {
            contexts.remove(key.toLowerCase());
        }
    }

    @Override
    public void removePlayerContexts(final UUID uuid) {
        this.standardContexts.remove(uuid);
    }

    @Override
    public void register(final String id, final Class<?> permissions) {
        for (final Field field : permissions.getDeclaredFields()) {
            final PermissionMetadata metadata = field.getAnnotation(PermissionMetadata.class);
            if (metadata != null && field.getType().equals(String.class)) {
                try {
                    field.setAccessible(true);
                    this.register((String) field.get(null), metadata, id);
                } catch (final IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void accumulateContexts(final Subject target, final Set<Context> accumulator) {
        if (target instanceof Identifiable) {
            final Map<String, Context> ctxs = this.standardContexts.get(((Identifiable) target).getUniqueId());
            if (ctxs != null && !ctxs.isEmpty()) {
                accumulator.addAll(ctxs.values());
            }
        }
    }

    @Override
    public boolean matches(final Context context, final Subject target) {
        if (target instanceof Identifiable) {
            final Map<String, Context> ctxs = this.standardContexts.get(((Identifiable) target).getUniqueId());
            if (ctxs != null && !ctxs.isEmpty()) {
                final Context ctx = ctxs.get(context.getKey());
                return ctx.equals(context);
            }
        }
        return false;
    }

    private int getDefaultLevel(final Subject subject) {
        if (subject instanceof SystemSubject || subject instanceof Server || subject instanceof CommandBlock) {
            return Integer.MAX_VALUE;
        }

        return 1;
    }

    public static class Metadata implements IPermissionService.Metadata {

        private final String description;
        private final String permission;
        private final SuggestedLevel suggestedLevel;
        private final boolean isPrefix;
        private final String[] replacements;
        private final String moduleid;

        Metadata(final String permission, final PermissionMetadata metadata, final String moduleid) {
            this(
                    metadata.descriptionKey(),
                    metadata.replacements(),
                    permission,
                    metadata.level(),
                    metadata.isPrefix(),
                    moduleid
            );
        }

        Metadata(final String description,
                final String[] replacements,
                final String permission,
                final SuggestedLevel suggestedLevel,
                final boolean isPrefix,
                final String moduleid) {
            this.description = description;
            this.replacements = replacements;
            this.permission = permission.toLowerCase();
            this.suggestedLevel = suggestedLevel;
            this.isPrefix = isPrefix;
            this.moduleid = moduleid;
        }

        @Override public boolean isPrefix() {
            return this.isPrefix;
        }

        @Override public SuggestedLevel getSuggestedLevel() {
            return this.suggestedLevel;
        }

        @Override public String getDescription(final IMessageProviderService service) {
            return service.getMessageString(this.description, (Object[]) this.replacements);
        }

        @Override public String getPermission() {
            return this.permission;
        }

        @Override public String getModuleId() {
            return this.moduleid;
        }

    }

}
