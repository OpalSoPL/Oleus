/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.quickstart;

import org.apache.logging.log4j.Logger;
import uk.co.drnaylor.quickstart.LoggerProxy;

public class NucleusLoggerProxy implements LoggerProxy {

    private final Logger logger;

    public NucleusLoggerProxy(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(final String message) {
        this.logger.info(message);
    }

    @Override
    public void warn(final String message) {
        this.logger.warn(message);
    }

    @Override
    public void error(final String message) {
        this.logger.error(message);
    }
}
