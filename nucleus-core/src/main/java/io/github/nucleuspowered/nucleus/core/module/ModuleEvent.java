/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.module;

import io.github.nucleuspowered.nucleus.api.core.event.NucleusModuleSelectionEvent;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.plugin.PluginContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModuleEvent extends AbstractEvent implements NucleusModuleSelectionEvent {

    private final Cause cause;
    private final Map<String, Boolean> moduleStatus = new HashMap<>();
    private final Set<String> allModules;

    public ModuleEvent(final Cause cause, final Set<String> allModules, final Set<String> moduleStatus) {
        this.cause = cause;
        this.allModules = Collections.unmodifiableSet(allModules);
        for (final String s : moduleStatus) {
            this.moduleStatus.put(s, true);
        }
    }

    @Override
    public Set<String> availableModules() {
        return this.allModules;
    }

    @Override
    public boolean disableModule(final String module, final PluginContainer plugin) throws IllegalArgumentException {
        if (!this.allModules.contains(module)) {
            throw new IllegalArgumentException("Module " + module + " does not exist");
        }
        if (this.moduleStatus.containsKey(module)) {
            this.moduleStatus.put(module, false);
            return true;
        }

        return false;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    public boolean shouldLoad(final String id) {
        return this.moduleStatus.getOrDefault(id, true);
    }

}
