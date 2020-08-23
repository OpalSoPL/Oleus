package io.github.nucleuspowered.nucleus.services.impl.messageprovider.template;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Map;

@FunctionalInterface
public interface TextElement {

    TextComponent retrieve(Map<String, Component> args);

}
