/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.listener;

import io.github.nucleuspowered.nucleus.core.scaffold.EntryPoint;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;

@EntryPoint
public interface ListenerBase {

    @EntryPoint
    interface Conditional extends ListenerBase {

        boolean shouldEnable(INucleusServiceCollection serviceCollection);
    }

}
