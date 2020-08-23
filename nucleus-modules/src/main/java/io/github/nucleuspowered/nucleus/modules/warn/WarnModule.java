/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.warn;

import io.github.nucleuspowered.nucleus.quickstart.module.StandardModule;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import uk.co.drnaylor.quickstart.annotations.ModuleData;
import uk.co.drnaylor.quickstart.holders.DiscoveryModuleHolder;

import java.util.function.Supplier;

import com.google.inject.Inject;

@ModuleData(id = "warn", name = "Warning")
public class WarnModule extends StandardModule {

    @Inject
    public WarnModule(final Supplier<DiscoveryModuleHolder<?, ?>> moduleHolder, final INucleusServiceCollection collection) {
        super(moduleHolder, collection);
    }

    @Override public void performPreTasks(final INucleusServiceCollection serviceCollection) {
        serviceCollection.logger()
                .warn("The Nucleus Warning module has been removed. Please use other plugins for warning support.");
    }
}
