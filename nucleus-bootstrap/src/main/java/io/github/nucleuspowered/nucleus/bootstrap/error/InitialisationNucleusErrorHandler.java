/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.bootstrap.error;

import io.github.nucleuspowered.nucleus.core.IPluginInfo;
import io.github.nucleuspowered.nucleus.core.startuperror.NucleusErrorHandler;
import org.apache.logging.log4j.Logger;
import org.spongepowered.plugin.PluginContainer;

public final class InitialisationNucleusErrorHandler extends NucleusErrorHandler {

    public InitialisationNucleusErrorHandler(final PluginContainer pluginContainer, final Throwable throwable,
            final boolean shouldShutdown,
            final Logger logger,
            final IPluginInfo pluginInfo) {
        super(pluginContainer, throwable, shouldShutdown, logger, pluginInfo);
    }

    @Override
    public String getTitle() {
        return "NUCLEUS - ERROR DURING INITIALISATION PHASE";
    }

}
