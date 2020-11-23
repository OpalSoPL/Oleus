/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.configurate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.core.configurate.typeserialisers.InstantTypeSerialiser;
import io.github.nucleuspowered.nucleus.core.configurate.typeserialisers.LocaleSerialiser;
import io.github.nucleuspowered.nucleus.core.configurate.typeserialisers.NamedLocationSerialiser;
import io.github.nucleuspowered.nucleus.core.configurate.typeserialisers.NucleusTextTemplateTypeSerialiser;
import io.github.nucleuspowered.nucleus.core.configurate.typeserialisers.PatternTypeSerialiser;
import io.github.nucleuspowered.nucleus.core.configurate.typeserialisers.Vector3dTypeSerialiser;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.impl.storage.DataObjectTranslator;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IConfigurateHelper;
import io.github.nucleuspowered.nucleus.core.services.interfaces.annotation.configuratehelper.LocalisedComment;
import io.github.nucleuspowered.nucleus.core.util.GeAnTyRefTypeTokens;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.math.vector.Vector3d;

import java.time.Instant;
import java.util.regex.Pattern;

@Singleton
public class ConfigurateHelper implements IConfigurateHelper {

    private final TypeSerializerCollection baseTypeSerializerCollection;
    private final ObjectMapper.Factory objectMapperFactory;
    private final TypeSerializerCollection.Builder moduleBuilderTSC = TypeSerializerCollection.builder();

    @Nullable
    private TypeSerializerCollection withModules = null;

    // TODO: If we can get the default Sponge ConfigurationOptions injected, do it here.
    @Inject
    public ConfigurateHelper(final INucleusServiceCollection serviceCollection,
            @DefaultConfig(sharedRoot = false) final HoconConfigurationLoader configurationLoader) {
        this.objectMapperFactory = ObjectMapper
                .factoryBuilder()
                .addProcessor(LocalisedComment.class, ObjectMapperActions.localisedComments(serviceCollection.messageProvider()))
                .addNodeResolver(ObjectMapperActions.defaultValue()) // Ideally, we don't want to do this in the long term.
                .build();
        this.baseTypeSerializerCollection = ConfigurateHelper.setupCore(
                serviceCollection,
                this.objectMapperFactory,
                configurationLoader.defaultOptions());
    }

    /**
     * Set Nucleus specific options on the {@link ConfigurationOptions}
     *
     * @param options The {@link ConfigurationOptions} to alter.
     * @return The {@link ConfigurationOptions}, for easier inline use of this function.
     */
    @Override
    public ConfigurationOptions setOptions(final ConfigurationOptions options) {
        // Allows us to use localised comments and @ProcessSetting annotations
        if (this.withModules != null) {
            return options.serializers(this.withModules);
        } else {
            return options.serializers(this.baseTypeSerializerCollection);
        }
    }

    @Override
    public void addTypeSerialiser(@Nullable final TypeSerializerCollection collection) {
        if (this.withModules != null) {
            throw new IllegalStateException("Modules have been initialised.");
        }
        if (collection != null) {
            this.moduleBuilderTSC.registerAll(collection);
        }
    }

    @Override
    public TypeSerializerCollection complete() {
        this.withModules = this.moduleBuilderTSC.build();
        return this.withModules;
    }

    private static TypeSerializerCollection setupCore(final INucleusServiceCollection serviceCollection,
            final ObjectMapper.Factory factory,
            final ConfigurationOptions options) {
        final TypeSerializerCollection.Builder typeSerializerCollection = options.serializers().childBuilder();

        // Custom type serialisers for Nucleus
        typeSerializerCollection.registerAnnotatedObjects(factory);
        typeSerializerCollection.register(TypeToken.get(Vector3d.class), new Vector3dTypeSerialiser());
        typeSerializerCollection.register(TypeToken.get(Pattern.class), new PatternTypeSerialiser());
        typeSerializerCollection.register(TypeToken.get(NucleusTextTemplate.class),
                new NucleusTextTemplateTypeSerialiser(serviceCollection.textTemplateFactory()));
        typeSerializerCollection.register(TypeToken.get(Instant.class), new InstantTypeSerialiser());

        typeSerializerCollection.register(GeAnTyRefTypeTokens.ABSTRACT_DATA_OBJECT_TYPE_TOKEN, DataObjectTranslator.INSTANCE);
        typeSerializerCollection.register(GeAnTyRefTypeTokens.NAMED_LOCATION, new NamedLocationSerialiser());
        typeSerializerCollection.register(GeAnTyRefTypeTokens.LOCALE, new LocaleSerialiser());

        return typeSerializerCollection.build();
    }

}
