/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.task;

import io.github.nucleuspowered.nucleus.Constants;
import io.github.nucleuspowered.nucleus.annotationprocessor.Store;
import io.github.nucleuspowered.nucleus.scaffold.EntryPoint;
import org.spongepowered.api.scheduler.Task;
import java.time.Duration;
import java.util.function.Consumer;

@EntryPoint
@Store(Constants.RUNNABLE)
public interface TaskBase extends Consumer<Task> {

    boolean isAsync();

    Duration interval();

}
