/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.messageprovider.template;

import net.kyori.adventure.text.Component;

import java.util.Map;

@FunctionalInterface
public interface TextElement {

    Component retrieve(Map<String, Component> args);

}
