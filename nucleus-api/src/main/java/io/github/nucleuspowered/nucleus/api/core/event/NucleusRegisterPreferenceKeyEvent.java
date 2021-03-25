package io.github.nucleuspowered.nucleus.api.core.event;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import org.spongepowered.api.event.Event;

public interface NucleusRegisterPreferenceKeyEvent extends Event {

    NucleusRegisterPreferenceKeyEvent register(NucleusUserPreferenceService.PreferenceKey<?> key);

}
