/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplateFactory;
import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.TemplateParser;
import net.kyori.adventure.text.Component;

@ImplementedBy(TemplateParser.class)
public interface INucleusTextTemplateFactory extends NucleusTextTemplateFactory {

    NucleusTextTemplateImpl empty();

    NucleusTextTemplateImpl createFromAmpersandString(String string);

    NucleusTextTemplateImpl createFromAmpersandString(String string, Component prefix, Component suffix);
}
