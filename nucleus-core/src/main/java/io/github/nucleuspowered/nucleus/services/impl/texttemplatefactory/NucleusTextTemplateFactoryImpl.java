/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory;

import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.INucleusTextTemplateFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NucleusTextTemplateFactoryImpl implements INucleusTextTemplateFactory {

    private final INucleusServiceCollection serviceCollection;
    private final NucleusTextTemplateImpl.Empty emptyInstance;

    @Inject
    public NucleusTextTemplateFactoryImpl(final INucleusServiceCollection serviceCollection) {
        this.serviceCollection = serviceCollection;
        this.emptyInstance = new NucleusTextTemplateImpl.Empty(serviceCollection);
    }

    public NucleusTextTemplateImpl.Empty emptyTextTemplate() {
        return this.emptyInstance;
    }

    @Override
    public NucleusTextTemplateImpl createFromString(final String string) {
        return this.create(string);
    }

    @Override
    public NucleusTextTemplateImpl createFromAmpersandString(final String string) {
        return new NucleusTextTemplateImpl.Ampersand(string, this.serviceCollection);
    }

    @Override public NucleusTextTemplateImpl createFromAmpersandString(final String string, final TextComponent prefix, final TextComponent suffix) {
        return new NucleusTextTemplateImpl.Ampersand(string, prefix, suffix, this.serviceCollection);
    }

    public NucleusTextTemplateImpl create(final String string) {
        if (string.isEmpty()) {
            return this.emptyInstance;
        }

        try {
            return new NucleusTextTemplateImpl.Json(string, this.serviceCollection);
        } catch (final NullPointerException e) {
            return this.createFromAmpersand(string);
        } catch (final RuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof ObjectMappingException) {
                return this.createFromAmpersand(string);
            } else {
                throw e;
            }
        }
    }

    private NucleusTextTemplateImpl createFromAmpersand(final String string) {
        return new NucleusTextTemplateImpl.Ampersand(string, this.serviceCollection);
    }

}
