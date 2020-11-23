/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces.annotation;

/**
 * For annotating option keys.
 */
public @interface OptionMetadata {

    /**
     * The translation key
     *
     * @return The key
     */
    String value();

}
