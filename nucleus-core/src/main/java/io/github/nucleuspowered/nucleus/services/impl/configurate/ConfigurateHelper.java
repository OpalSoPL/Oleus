/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.configurate;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.github.nucleuspowered.neutrino.objectmapper.NeutrinoObjectMapperFactory;
import io.github.nucleuspowered.neutrino.settingprocessor.SettingProcessor;
import io.github.nucleuspowered.neutrino.typeserialisers.ByteArrayTypeSerialiser;
import io.github.nucleuspowered.neutrino.typeserialisers.IntArrayTypeSerialiser;
import io.github.nucleuspowered.neutrino.typeserialisers.PatternTypeSerialiser;
import io.github.nucleuspowered.neutrino.typeserialisers.SetTypeSerialiser;
import io.github.nucleuspowered.neutrino.typeserialisers.ShortArrayTypeSerialiser;
import io.github.nucleuspowered.neutrino.util.ClassConstructor;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.InstantTypeSerialiser;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.LocaleSerialiser;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.NamedLocationSerialiser;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.NucleusTextTemplateTypeSerialiser;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.Vector3dTypeSerialiser;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.storage.DataObjectTranslator;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.configurate.AbstractConfigurateBackedDataObject;
import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.services.interfaces.IConfigurateHelper;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.util.TypeTokens;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.math.vector.Vector3d;

import java.time.Instant;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class ConfigurateHelper implements IConfigurateHelper {

    private final IMessageProviderService messageProvider;

    private static final TypeToken<AbstractConfigurateBackedDataObject> ABSTRACT_DATA_OBJECT_TYPE_TOKEN = TypeToken.of(
            AbstractConfigurateBackedDataObject.class);

    private final TypeSerializerCollection baseTypeSerializerCollection;
    private final NeutrinoObjectMapperFactory objectMapperFactory;
    private final Pattern commentPattern = Pattern.compile("^(loc:)?(?<key>([a-zA-Z0-9_-]+\\.?)+)$");

    @Nullable private TypeSerializerCollection withModules = null;

    @Inject
    public ConfigurateHelper(final INucleusServiceCollection serviceCollection) {
        this.messageProvider = serviceCollection.messageProvider();
        this.objectMapperFactory = NeutrinoObjectMapperFactory.builder()
                .setCommentProcessor(setting -> {
                    final String comment = setting.comment();
                    if (comment.contains(".") && !comment.contains(" ")) {
                        final Matcher matcher = this.commentPattern.matcher(comment);

                        if (matcher.matches()) {
                            return this.messageProvider.getMessageString(matcher.group("key"));
                        }
                    }

                    return comment;
                })
                .setSettingProcessorClassConstructor(new SettingProcessorConstructor(serviceCollection.injector()))
                .build(true);
        this.baseTypeSerializerCollection = ConfigurateHelper.setup(serviceCollection);
    }

    /**
     * Set NucleusPlugin specific options on the {@link ConfigurationOptions}
     *
     * @param options The {@link ConfigurationOptions} to alter.
     * @return The {@link ConfigurationOptions}, for easier inline use of this function.
     */
    @Override
    public ConfigurationOptions setOptions(final ConfigurationOptions options) {
        // Allows us to use localised comments and @ProcessSetting annotations
        final ConfigurationOptions configurationOptions = options.withObjectMapperFactory(this.objectMapperFactory);
        if (this.withModules != null) {
            return configurationOptions.withSerializers(this.withModules);
        } else {
            return configurationOptions.withSerializers(this.baseTypeSerializerCollection);
        }
    }

    private static TypeSerializerCollection setup(final INucleusServiceCollection serviceCollection) {
        final TypeSerializerCollection typeSerializerCollection = ConfigurationOptions.defaults().getSerializers().newChild();

        // Custom type serialisers for Nucleus
        typeSerializerCollection.register(TypeToken.of(Vector3d.class), new Vector3dTypeSerialiser());
        typeSerializerCollection.register(TypeToken.of(Pattern.class), new PatternTypeSerialiser());
        typeSerializerCollection.register(TypeToken.of(NucleusTextTemplateImpl.class), new NucleusTextTemplateTypeSerialiser(serviceCollection.textTemplateFactory()));
        typeSerializerCollection.register(
                typeToken -> Set.class.isAssignableFrom(typeToken.getRawType()),
                new SetTypeSerialiser()
        );

        typeSerializerCollection.register(new TypeToken<byte[]>(){}, new ByteArrayTypeSerialiser());
        typeSerializerCollection.register(new TypeToken<short[]>(){}, new ShortArrayTypeSerialiser());
        typeSerializerCollection.register(new TypeToken<int[]>(){}, new IntArrayTypeSerialiser());
        typeSerializerCollection.register(TypeToken.of(Instant.class), new InstantTypeSerialiser());

        typeSerializerCollection.register(x -> x.isSubtypeOf(ABSTRACT_DATA_OBJECT_TYPE_TOKEN), DataObjectTranslator.INSTANCE);
        typeSerializerCollection.register(TypeTokens.NAMEDLOCATION, new NamedLocationSerialiser());
        typeSerializerCollection.register(TypeTokens.LOCALE, new LocaleSerialiser());

        return typeSerializerCollection;
    }

    private static final class SettingProcessorConstructor implements ClassConstructor<SettingProcessor> {

        private final Injector injector;

        private SettingProcessorConstructor(final Injector injector) {
            this.injector = injector;
        }

        @Override public <T extends SettingProcessor> T construct(final Class<T> aClass) throws Throwable {
            return this.injector.getInstance(aClass);
        }
    }
}
