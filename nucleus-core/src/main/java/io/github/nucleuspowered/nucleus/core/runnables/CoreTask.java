/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.runnables;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.core.config.CoreConfig;
import io.github.nucleuspowered.nucleus.scaffold.task.TaskBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IReloadableService;
import org.spongepowered.api.scheduler.Task;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

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
    public boolean isAsync() {
        return true;
    }

    @Override
    public Duration interval() {
        return Duration.of(5, ChronoUnit.MINUTES);
    }

    @Override
    public void accept(final Task task) {
        this.serviceCollection.storageManager().getUserService().clearCache();

        if (this.printSave) {
            this.serviceCollection.logger().info(this.serviceCollection.messageProvider().getMessageString("core.savetask.starting"));
        }

        this.serviceCollection.storageManager().saveAll();

        if (this.printSave) {
            this.serviceCollection.logger().info(this.serviceCollection.messageProvider().getMessageString("core.savetask.complete"));
        }
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.printSave = serviceCollection.moduleDataProvider().getModuleConfig(CoreConfig.class).isPrintOnAutosave();
    }

}
