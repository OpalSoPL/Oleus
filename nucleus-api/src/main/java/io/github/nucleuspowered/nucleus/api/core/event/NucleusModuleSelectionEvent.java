/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.core.event;

import org.spongepowered.api.event.Event;
import org.spongepowered.plugin.PluginContainer;

import java.util.Set;

/**
 * A set of events that fire at various points of the NucleusPlugin lifecycle.
 */
public interface NucleusModuleSelectionEvent extends Event {

    /**
     * Gets the modules that Nucleus wishes to load.
     *
     * @return A set of module IDs.
     */
    Set<String> availableModules();

    /**
     * Disables the named module.
     *
     * @param module The id of the module to disable.
     * @param plugin The plugin that is requesting to disable the module. Used for logging purposes - telling the
     *               user who is disabling the plugin.
     * @return {@code true} if the module was disabled.
     * @throws IllegalArgumentException Thrown if the module does not exist.
     */
    boolean disableModule(String module, PluginContainer plugin) throws IllegalArgumentException;

}
