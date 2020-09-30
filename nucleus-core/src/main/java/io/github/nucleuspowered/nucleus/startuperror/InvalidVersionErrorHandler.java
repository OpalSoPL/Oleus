/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.startuperror;

import io.github.nucleuspowered.nucleus.util.PrettyPrinter;
import org.apache.logging.log4j.Logger;
import org.spongepowered.plugin.PluginContainer;

public final class InvalidVersionErrorHandler extends NucleusErrorHandler {

    private final String versionRequested;

    public InvalidVersionErrorHandler(final PluginContainer pluginContainer,
            final boolean shouldShutDown,
            final Logger logger,
            final String versionRequested) {
        super(pluginContainer, null, shouldShutDown, logger);
        this.versionRequested = versionRequested;
    }

    @Override
    public String getTitle() {
        return "NUCLEUS - INCORRECT SPONGE API VERSION";
    }

    @Override
    protected void createTopLevelMessage(final PrettyPrinter prettyPrinter) {
        prettyPrinter.addWrapped("You are running "
                + "a mismatched version of Sponge on your server - this version of Nucleus will not run "
                + "upon it.");
        prettyPrinter.addWrapped("Nucleus has not started. Make sure you're running SpongeAPI %s and try again.",
                this.versionRequested);
    }
}
