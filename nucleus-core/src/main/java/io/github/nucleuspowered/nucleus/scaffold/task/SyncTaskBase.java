package io.github.nucleuspowered.nucleus.scaffold.task;

import org.spongepowered.api.scheduler.ScheduledTask;

import java.time.Duration;
import java.util.function.Consumer;

public interface SyncTaskBase extends Consumer<ScheduledTask> {

    Duration interval();

}
