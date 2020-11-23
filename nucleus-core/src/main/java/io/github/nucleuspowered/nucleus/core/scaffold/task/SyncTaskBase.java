/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.scaffold.task;

import org.spongepowered.api.scheduler.ScheduledTask;

import java.time.Duration;
import java.util.function.Consumer;

public interface SyncTaskBase extends Consumer<ScheduledTask> {

    Duration interval();

}
