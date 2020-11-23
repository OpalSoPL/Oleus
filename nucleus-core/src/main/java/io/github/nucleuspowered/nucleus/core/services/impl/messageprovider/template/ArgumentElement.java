/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.messageprovider.template;

import io.github.nucleuspowered.nucleus.core.services.interfaces.ITextStyleService;
import net.kyori.adventure.text.Component;

import java.util.Map;

public final class ArgumentElement implements TextElement {

    final ITextStyleService.TextFormat format;
    final String key;

    public ArgumentElement(final ITextStyleService.TextFormat format, final String key) {
        this.format = format;
        this.key = key;
    }

    @Override
    public Component retrieve(final Map<String, Component> args) {
        final Component component = args.get(this.key);
        if (component == null) {
            return Component.empty();
        }

        return null;
    }
}
