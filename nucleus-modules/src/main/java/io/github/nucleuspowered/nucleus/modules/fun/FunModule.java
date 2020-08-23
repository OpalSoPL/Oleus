/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.fun;

import io.github.nucleuspowered.nucleus.quickstart.module.StandardModule;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import uk.co.drnaylor.quickstart.annotations.ModuleData;
import uk.co.drnaylor.quickstart.holders.DiscoveryModuleHolder;

import java.util.function.Supplier;

import com.google.inject.Inject;

@ModuleData(id = "fun", name = "Fun")
public class FunModule extends StandardModule {

    @Inject
    public FunModule(final Supplier<DiscoveryModuleHolder<?, ?>> moduleHolder, final INucleusServiceCollection collection) {
        super(moduleHolder, collection);
    }
}
