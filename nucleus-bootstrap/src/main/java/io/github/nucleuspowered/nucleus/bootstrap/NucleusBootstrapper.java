package io.github.nucleuspowered.nucleus.bootstrap;

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.github.nucleuspowered.nucleus.OldNucleusCore;
import io.github.nucleuspowered.nucleus.modules.NucleusModuleProvider;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

import java.nio.file.Path;

@Plugin("nucleus")
public class NucleusBootstrapper {

    private final PluginContainer pluginContainer;
    private final Logger logger;
    private final Path configDirectory;
    private final Injector injector;

    @Inject
    public NucleusBootstrapper(
            final PluginContainer pluginContainer,
            final Logger logger,
            @ConfigDir(sharedRoot = false) final Path configDirectory,
            final Injector injector) {
        this.pluginContainer = pluginContainer;
        this.logger = logger;
        this.configDirectory = configDirectory;
        this.injector = injector;
    }

    @Listener
    public void startPlugin(final ConstructPluginEvent event) {
        this.logger.info("Nucleus is starting.");
        final OldNucleusCore core = new OldNucleusCore(this.pluginContainer, this.configDirectory, this.logger, this.injector, new NucleusModuleProvider());
        Sponge.getEventManager().registerListeners(this.pluginContainer, core);
    }

}
