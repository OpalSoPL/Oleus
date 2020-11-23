/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LocalisedComment {

    /**
     * The translation key.
     *
     * @return The translation key.
     */
    String value();

    /**
     * The token replacements in the key.
     *
     * @return The replacements.
     */
    String[] replacements() default {};

}
