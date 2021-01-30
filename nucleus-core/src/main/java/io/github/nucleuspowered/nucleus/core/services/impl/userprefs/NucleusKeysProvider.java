/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.userprefs;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.DefaultedRegistryType;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class NucleusKeysProvider implements NucleusUserPreferenceService.Keys {

    public final static ResourceKey COMMAND_SPY_KEY = ResourceKey.resolve("nucleus:command-spy");
    public final static ResourceKey MESSAGE_TOGGLE_KEY = ResourceKey.resolve("nucleus:message-receiving-enabled");
    public final static ResourceKey PLAYER_LOCALE_KEY = ResourceKey.resolve("nucleus:player-locale");
    public final static ResourceKey POWERTOOL_ENABLED_KEY = ResourceKey.resolve("nucleus:powertool-toggle");
    public final static ResourceKey SOCIAL_SPY_KEY = ResourceKey.resolve("nucleus:social-spy");
    public final static ResourceKey TELEPORT_TARGETABLE_KEY = ResourceKey.resolve("nucleus:teleport-targetable");
    public final static ResourceKey VANISH_ON_LOGIN_KEY = ResourceKey.resolve("nucleus:vanish-on-login");
    public final static ResourceKey VIEW_STAFF_CHAT_KEY = ResourceKey.resolve("nucleus:view-staff-chat");

    private final DefaultedRegistryType<NucleusUserPreferenceService.PreferenceKey<?>> registryType;

    public NucleusKeysProvider(final DefaultedRegistryType<NucleusUserPreferenceService.PreferenceKey<?>> registryType) {
        this.registryType = registryType;
    }

    public Collection<NucleusUserPreferenceService.PreferenceKey<?>> getAll() {
        return this.registryType.get().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> vanishOnLogin() {
        return this.get(NucleusKeysProvider.VANISH_ON_LOGIN_KEY);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> teleportTarget() {
        return this.get(NucleusKeysProvider.TELEPORT_TARGETABLE_KEY);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> powertoolsEnabled() {
        return this.get(NucleusKeysProvider.POWERTOOL_ENABLED_KEY);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> socialSpyEnabled() {
        return this.get(NucleusKeysProvider.SOCIAL_SPY_KEY);
    }

    @Override public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> messageReceivingEnabled() {
        return this.get(NucleusKeysProvider.MESSAGE_TOGGLE_KEY);
    }

    @Override
    public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> commandSpyEnabled() {
        return this.get(NucleusKeysProvider.COMMAND_SPY_KEY);
    }

    @Override
    public Optional<NucleusUserPreferenceService.PreferenceKey<Boolean>> viewStaffChat() {
        return this.get(NucleusKeysProvider.VIEW_STAFF_CHAT_KEY);
    }

    @Override
    public Optional<NucleusUserPreferenceService.PreferenceKey<Locale>> playerLocale() {
        return this.get(NucleusKeysProvider.PLAYER_LOCALE_KEY);
    }

    private <T> Optional<NucleusUserPreferenceService.PreferenceKey<T>> get(final ResourceKey key) {
        return this.registryType.get().findValue(key);
    }

}
