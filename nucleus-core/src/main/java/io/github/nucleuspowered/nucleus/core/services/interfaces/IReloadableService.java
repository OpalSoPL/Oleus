/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.reloadable.ReloadableService;

@ImplementedBy(ReloadableService.class)
public interface IReloadableService {

    void registerEarlyReloadable(Reloadable reloadable);

    void registerReloadable(Reloadable reloadable);

    void fireReloadables(INucleusServiceCollection serviceCollection);

    void registerDataFileReloadable(DataLocationReloadable dataLocationReloadable);

    void fireDataFileReloadables(INucleusServiceCollection serviceCollection);

    void removeReloadable(Reloadable reloadable);

    interface Reloadable {

        void onReload(INucleusServiceCollection serviceCollection);

    }

    interface DataLocationReloadable {

        void onDataFileLocationChange(INucleusServiceCollection serviceCollection);

    }
}
