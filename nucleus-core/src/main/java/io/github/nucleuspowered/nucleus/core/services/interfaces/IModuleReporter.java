/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.core.module.ModuleContainer;
import io.github.nucleuspowered.nucleus.core.services.impl.modulereporter.ModuleReporter;

import java.util.Collection;

@ImplementedBy(ModuleReporter.class)
public interface IModuleReporter {

    Collection<String> discoveredModules();

    Collection<ModuleContainer> enabledModules();

    void provideDiscoveredModules(Collection<String> discoveredModules);

    void provideEnabledModule(ModuleContainer moduleContainer);

    boolean isLoaded(String module);
}
