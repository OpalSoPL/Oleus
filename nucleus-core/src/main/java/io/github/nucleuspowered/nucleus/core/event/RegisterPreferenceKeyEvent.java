/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.event;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import io.github.nucleuspowered.nucleus.api.core.event.NucleusRegisterPreferenceKeyEvent;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.registry.DefaultedRegistryType;

public class RegisterPreferenceKeyEvent implements NucleusRegisterPreferenceKeyEvent {

    private final DefaultedRegistryType<NucleusUserPreferenceService.PreferenceKey<?>> registry;
    private final Cause cause;

    public RegisterPreferenceKeyEvent(final DefaultedRegistryType<NucleusUserPreferenceService.PreferenceKey<?>> registry,
            final Cause cause) {
        this.registry = registry;
        this.cause = cause;
    }

    @Override
    public NucleusRegisterPreferenceKeyEvent register(final NucleusUserPreferenceService.PreferenceKey<?> key) {
        this.registry.get().register(key.getKey(), key);
        return this;
    }

    @Override
    public Cause cause() {
        return this.cause;
    }

}
