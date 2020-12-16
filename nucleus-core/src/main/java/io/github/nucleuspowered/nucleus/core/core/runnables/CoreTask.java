/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.core.runnables;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.core.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.Identifiable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * Core tasks. No module, must always run.
 */
public class CoreTask implements TaskBase, IReloadableService.Reloadable {

    private boolean printSave = false;
    private final INucleusServiceCollection serviceCollection;

    @Inject
    public CoreTask(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
    }

    @Override
    public Duration interval() {
        return Duration.of(5, ChronoUnit.MINUTES);
    }

    @Override
    public void run() {
        if (this.printSave) {
            this.serviceCollection.logger().info(this.serviceCollection.messageProvider().getMessageString("core.savetask.starting"));
        }

        // Only do maintenance on the cache once it's been saved.
        this.serviceCollection.storageManager().saveAll().thenAccept(x -> {
            if (this.printSave) {
                this.serviceCollection.logger().info(this.serviceCollection.messageProvider().getMessageString("core.savetask.complete"));
            }
            this.serviceCollection.storageManager().getUserService().clearCacheUnless(
                    Sponge.getServer().getOnlinePlayers().stream().map(Identifiable::getUniqueId).collect(Collectors.toSet()));
        });

    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.printSave = serviceCollection.configProvider().getModuleConfig(CoreConfig.class).isPrintOnAutosave();
    }

}
