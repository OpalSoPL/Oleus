/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.ban;

import io.github.nucleuspowered.nucleus.modules.ban.config.BanConfig;
import io.github.nucleuspowered.nucleus.modules.ban.config.BanConfigAdapter;
import io.github.nucleuspowered.nucleus.quickstart.module.ConfigurableModule;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import uk.co.drnaylor.quickstart.annotations.ModuleData;
import uk.co.drnaylor.quickstart.holders.DiscoveryModuleHolder;

import java.util.function.Supplier;

import com.google.inject.Inject;

@ModuleData(id = "ban", name = "Bans")
public class BanModule extends ConfigurableModule<BanConfig, BanConfigAdapter> {

    public static final String ID = "ban";

    @Inject
    public BanModule(final Supplier<DiscoveryModuleHolder<?, ?>> moduleHolder,
            final INucleusServiceCollection collection) {
        super(moduleHolder, collection);
    }

    @Override
    public BanConfigAdapter createAdapter() {
        return new BanConfigAdapter();
    }

}
