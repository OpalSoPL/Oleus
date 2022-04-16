/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.docgen;

import com.google.inject.Inject;
import io.github.nucleuspowered.docgen.module.DocgenModule;
import io.github.nucleuspowered.nucleus.core.NucleusJavaProperties;
import io.github.nucleuspowered.nucleus.core.event.RegisterModuleEvent;
import io.github.nucleuspowered.nucleus.core.module.ModuleContainer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@Plugin("nucleus-docgen")
public class NucleusDocgenPlugin {

    public final static String MODULE_ID = "docgen";

    private final Logger logger;

    @Inject
    public NucleusDocgenPlugin(final Logger logger) {
        this.logger = logger;
    }

    @Listener
    public void onModuleContainerRegistration(final RegisterModuleEvent event) {
        this.logger.info("Injecting DocGen module");
        event.registerModuleProvider(
                () -> Collections.singleton(ModuleContainer.createContainer(NucleusDocgenPlugin.MODULE_ID, "Docgen", DocgenModule.class)));
    }

}
