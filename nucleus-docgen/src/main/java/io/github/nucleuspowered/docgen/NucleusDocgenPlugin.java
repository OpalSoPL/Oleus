/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.docgen;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("nucleus-docgen")
public class NucleusDocgenPlugin {

    private final Logger logger;

    @Inject
    public NucleusDocgenPlugin(final Logger logger) {
        this.logger = logger;
    }
}
