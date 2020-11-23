/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.reloadable;

import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Singleton;

@Singleton
public class ReloadableService implements IReloadableService {

    private final Set<Reloadable> earlyReloadables = new HashSet<>();
    private final Set<Reloadable> reloadables = new HashSet<>();
    private final Set<DataLocationReloadable> dataLocationReloadables = new HashSet<>();

    @Override public void registerEarlyReloadable(final Reloadable reloadable) {
        this.earlyReloadables.add(reloadable);
    }

    @Override public void registerReloadable(final Reloadable reloadable) {
        this.reloadables.add(reloadable);
    }

    @Override public void fireReloadables(final INucleusServiceCollection serviceCollection) {
        for (final Reloadable reloadable : this.earlyReloadables) {
            reloadable.onReload(serviceCollection);
        }

        for (final Reloadable reloadable1 : this.reloadables) {
            reloadable1.onReload(serviceCollection);
        }
    }

    @Override public void registerDataFileReloadable(final DataLocationReloadable dataLocationReloadable) {
        this.dataLocationReloadables.add(dataLocationReloadable);
    }

    @Override public void fireDataFileReloadables(final INucleusServiceCollection serviceCollection) {
        for (final DataLocationReloadable dataLocationReloadable : this.dataLocationReloadables) {
            dataLocationReloadable.onDataFileLocationChange(serviceCollection);
        }
    }
}
