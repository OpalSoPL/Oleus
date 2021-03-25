/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.core.event;

import io.github.nucleuspowered.nucleus.api.core.NucleusUserPreferenceService;
import org.spongepowered.api.event.Event;

public interface NucleusRegisterPreferenceKeyEvent extends Event {

    NucleusRegisterPreferenceKeyEvent register(NucleusUserPreferenceService.PreferenceKey<?> key);

}
