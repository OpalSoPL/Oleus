/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.gson.JsonArray;
import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.compatibility.CompatibilityService;

import java.util.Collection;

@ImplementedBy(CompatibilityService.class)
public interface ICompatibilityService {

    Collection<CompatibilityMessages> getMessages();

    Collection<CompatibilityMessages> getApplicableMessages();

    void set(JsonArray messages);

    interface CompatibilityMessages {

        String getModId();

        Collection<String> getModules();

        Severity getSeverity();

        String getSymptom();

        String getMessage();

        String getResolution();

    }

    enum Severity {
        CRITICAL(3),
        MAJOR(2),
        MINOR(1),
        INFORMATIONAL(0);

        private final int index;

        Severity(final int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }
    }

}
