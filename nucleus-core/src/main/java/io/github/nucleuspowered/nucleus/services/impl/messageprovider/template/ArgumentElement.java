package io.github.nucleuspowered.nucleus.services.impl.messageprovider.template;

import io.github.nucleuspowered.nucleus.services.interfaces.ITextStyleService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Map;

public final class ArgumentElement implements TextElement {

    final ITextStyleService.TextFormat format;
    final String key;

    public ArgumentElement(final ITextStyleService.TextFormat format, final String key) {
        this.format = format;
        this.key = key;
    }

    @Override
    public TextComponent retrieve(final Map<String, Component> args) {
        final Component component = args.get(this.key);
        if (component == null) {
            return TextComponent.empty();
        }

        return null;
    }
}
