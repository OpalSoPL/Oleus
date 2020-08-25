/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.task;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;

public abstract class CostCancellableTask implements CancellableTask {

    private final INucleusServiceCollection serviceCollection;
    @Nullable private final UUID target;
    private final double cost;
    private boolean hasRun = false;

    public CostCancellableTask(final INucleusServiceCollection serviceCollection, @Nullable final UUID target, final double cost) {
        this.serviceCollection = serviceCollection;
        this.target = target;
        this.cost = cost;
    }

    @Override
    public void onCancel() {
        if (!this.hasRun) {
            this.hasRun = true;
            if (this.target != null && this.cost > 0) {
                Task.builder()
                        .execute(task -> this.serviceCollection.economyServiceProvider().depositInPlayer(this.target, this.cost))
                        .plugin(this.serviceCollection.pluginContainer())
                        .build();
            }
        }
    }
}
