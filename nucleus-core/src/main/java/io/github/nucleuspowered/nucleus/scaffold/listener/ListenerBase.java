/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.listener;

import io.github.nucleuspowered.nucleus.scaffold.EntryPoint;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;

@EntryPoint
public interface ListenerBase {

    @EntryPoint
    interface Conditional extends ListenerBase {

        boolean shouldEnable(INucleusServiceCollection serviceCollection);
    }

}
