/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.startuperror;

import io.github.nucleuspowered.nucleus.util.PrettyPrinter;
import org.apache.logging.log4j.Logger;
import org.spongepowered.plugin.PluginContainer;

public final class ConfigErrorHandler extends NucleusErrorHandler {

    private final String file;

    public ConfigErrorHandler(
            final PluginContainer pluginContainer,
            final Throwable throwable,
            final boolean shouldShutDown,
            final Logger logger,
            final String file) {
        super(pluginContainer, throwable, shouldShutDown, logger);
        this.file = file;
    }

    @Override public String getTitle() {
        return "NUCLEUS CONFIGURATION FAILED TO LOAD (" + this.file + ")";
    }

    @Override protected void createPostStackTraceMessage(final PrettyPrinter prettyPrinter) {
        // it's a config error.
    }

    @Override
    protected void createTopLevelMessage(final PrettyPrinter prettyPrinter) {
        prettyPrinter.add("One of your configuration files (%s) is broken and could not be read.", this.file);
        prettyPrinter.add();
        prettyPrinter.add(this.capturedThrowable.getCause().getMessage());
        prettyPrinter.add();
        prettyPrinter.add("This is usually because you've added an ID but forgotten to surround the ID with double quotes "
                + "(\").");
        prettyPrinter.add("For example, to add minecraft:stone to the config, it should be added as \"minecraft:stone\"");
        prettyPrinter.add();
        prettyPrinter.add("You will need to fix the configuration file and restart your server.");
    }
}
