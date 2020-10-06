package io.github.nucleuspowered.nucleus.services.interfaces.annotation.configuratehelper;

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
