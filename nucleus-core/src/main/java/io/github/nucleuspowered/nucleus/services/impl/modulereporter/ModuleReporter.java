package io.github.nucleuspowered.nucleus.services.impl.modulereporter;

import io.github.nucleuspowered.nucleus.module.ModuleContainer;
import io.github.nucleuspowered.nucleus.services.interfaces.IModuleReporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class ModuleReporter implements IModuleReporter {

    private final Collection<String> discoveredModules = new ArrayList<>();
    private final Collection<ModuleContainer> enabledModules = new HashSet<>();

    @Override
    public Collection<String> discoveredModules() {
        return Collections.unmodifiableCollection(this.discoveredModules);
    }

    @Override
    public Collection<ModuleContainer> enabledModules() {
        return Collections.unmodifiableCollection(this.enabledModules);
    }

    @Override
    public void provideDiscoveredModules(final Collection<String> discoveredModules) {
        if (this.discoveredModules.isEmpty()) {
            this.discoveredModules.addAll(discoveredModules);
        }
    }

    @Override
    public void provideEnabledModule(final ModuleContainer moduleContainer) {
        this.enabledModules.add(moduleContainer);
    }

}
