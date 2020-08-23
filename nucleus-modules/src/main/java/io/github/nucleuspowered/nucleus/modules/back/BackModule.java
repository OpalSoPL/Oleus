/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.back;

import io.github.nucleuspowered.nucleus.modules.back.config.BackConfig;
import io.github.nucleuspowered.nucleus.modules.back.config.BackConfigAdapter;
import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import uk.co.drnaylor.quickstart.annotations.ModuleData;
import uk.co.drnaylor.quickstart.holders.DiscoveryModuleHolder;

import java.util.function.Supplier;

import com.google.inject.Inject;

@ModuleData(id = BackModule.ID, name = "Back", softDependencies = "jail")
public class BackModule extends ConfigurableModule<BackConfig, BackConfigAdapter> {

    public static final String ID = "back";

    @Inject
    public BackModule(final Supplier<DiscoveryModuleHolder<?, ?>> moduleHolder, final INucleusServiceCollection collection) {
        super(moduleHolder, collection);
    }

    @Override
    public BackConfigAdapter createAdapter() {
        return new BackConfigAdapter();
    }

}
