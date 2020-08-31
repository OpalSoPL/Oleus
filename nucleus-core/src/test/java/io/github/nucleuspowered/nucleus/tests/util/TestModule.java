/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.tests.util;

import com.google.inject.AbstractModule;
import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        Path test;
        Path test2;
        try {
            test = Files.createTempDirectory("quick");
            test2 = Files.createTempFile(test, "quick", "conf");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        this.bind(Path.class).annotatedWith(DefaultConfig.class).toInstance(test2);
        this.bind(Path.class).annotatedWith(ConfigDir.class).toInstance(test);
        this.bind(Game.class).toInstance(Mockito.mock(Game.class));
        this.bind(Logger.class).toInstance(Mockito.mock(Logger.class));

        CoreConfigAdapter mock = Mockito.mock(CoreConfigAdapter.class);
        PowerMockito.replace(PowerMockito.method(CoreConfigAdapter.class, "getNode")).with((obj, method, arguments) -> new CoreConfig());

        this.bind(CoreConfigAdapter.class).toInstance(mock);

        try {
            OldNucleusCore plugin = getMockPlugin();
            this.bind(OldNucleusCore.class).toInstance(plugin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private OldNucleusCore getMockPlugin() {
        return Mockito.mock(OldNucleusCore.class);
    }
}
