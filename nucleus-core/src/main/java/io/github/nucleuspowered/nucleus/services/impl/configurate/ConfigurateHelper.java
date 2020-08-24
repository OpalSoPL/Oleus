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
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.MailMessageSerialiser;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.NamedLocationSerialiser;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.NucleusTextTemplateTypeSerialiser;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.Vector3dTypeSerialiser;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.WarpCategorySerialiser;
import io.github.nucleuspowered.nucleus.configurate.typeserialisers.WarpSerialiser;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.impl.storage.DataObjectTranslator;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.configurate.AbstractConfigurateBackedDataObject;
import io.github.nucleuspowered.nucleus.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.services.interfaces.IConfigurateHelper;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import io.github.nucleuspowered.nucleus.util.TypeTokens;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.math.vector.Vector3d;

import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class ConfigurateHelper implements IConfigurateHelper {

    private final IMessageProviderService messageProvider;

    private static final TypeToken<AbstractConfigurateBackedDataObject> ABSTRACT_DATA_OBJECT_TYPE_TOKEN = TypeToken.of(
            AbstractConfigurateBackedDataObject.class);

    private final TypeSerializerCollection typeSerializerCollection;
    private final NeutrinoObjectMapperFactory objectMapperFactory;
    private final Pattern commentPattern = Pattern.compile("^(loc:)?(?<key>([a-zA-Z0-9_-]+\\.?)+)$");

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
        this.typeSerializerCollection = setup(serviceCollection);
    }

    /**
     * Set NucleusPlugin specific options on the {@link ConfigurationOptions}
     *
     * @param options The {@link ConfigurationOptions} to alter.
     * @return The {@link ConfigurationOptions}, for easier inline use of this function.
     */
    @Override public ConfigurationOptions setOptions(final ConfigurationOptions options) {
        // Allows us to use localised comments and @ProcessSetting annotations
        return options.setSerializers(this.typeSerializerCollection).setObjectMapperFactory(this.objectMapperFactory);
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
        typeSerializerCollection.register(TypeTokens.WARP, WarpSerialiser.INSTANCE);
        typeSerializerCollection.register(TypeTokens.WARP_CATEGORY, new WarpCategorySerialiser());
        typeSerializerCollection.register(TypeTokens.NAMEDLOCATION, new NamedLocationSerialiser());
        typeSerializerCollection.register(TypeTokens.MAIL_MESSAGE, new MailMessageSerialiser());
        typeSerializerCollection.register(TypeTokens.LOCALE, new LocaleSerialiser());

        return typeSerializerCollection;
    }

    private static final String CWBAH_OBJECT_MAPPER = "cz.neumimto.config.blackjack.and.hookers.NotSoStupidObjectMapper";
    private static final String CWBAH_TYPE_SERIALIZER = "cz.neumimto.config.blackjack.and.hookers.ClassTypeNodeSerializer";
    private static final String ANNOTATED_OBJECT_SERIALIZER = "ninja.leaping.configurate.objectmapping.serialize.AnnotatedObjectSerializer";

    /**
     * Mostly taken from
     * https://github.com/SpongePowered/Configurate/blob/7e82385eb8a7f66eeb9cd6957dfe9470f3760198/configurate-core/src/main/java/ninja/leaping/configurate/objectmapping/serialize/AnnotatedObjectSerializer.java#L1
     *
     * Configurate
     * Copyright (C) zml and Configurate contributors
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *    http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    private static class AnnotatedObjectSerializer implements TypeSerializer<Object> {
        public static final String CLASS_KEY = "__class__";

        @Override
        public Object deserialize(@NonNull final TypeToken<?> type, @NonNull final ConfigurationNode value) throws ObjectMappingException {
            final TypeToken<?> clazz = this.getInstantiableType(type, value.getNode(CLASS_KEY).getString());
            // TODO: Fix for 3.7
            return value.getOptions().getObjectMapperFactory().getMapper(clazz.getRawType()).bindToNew().populate(value);
        }

        private TypeToken<?> getInstantiableType(final TypeToken<?> type, final String configuredName) throws ObjectMappingException {
            final TypeToken<?> retClass;
            final Class<?> rawType = type.getRawType();
            if (rawType.isInterface() || Modifier.isAbstract(rawType.getModifiers())) {
                if (configuredName == null) {
                    throw new ObjectMappingException("No available configured type for instances of " + type);
                } else {
                    try {
                        retClass = TypeToken.of(Class.forName(configuredName));
                    } catch (final ClassNotFoundException e) {
                        throw new ObjectMappingException("Unknown class of object " + configuredName, e);
                    }
                    if (!retClass.isSubtypeOf(type)) {
                        throw new ObjectMappingException("Configured type " + configuredName + " does not extend "
                                + rawType.getCanonicalName());
                    }
                }
            } else {
                retClass = type;
            }
            return retClass;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void serialize(@NonNull final TypeToken<?> type, @Nullable final Object obj, @NonNull final ConfigurationNode value) throws ObjectMappingException {
            if (obj == null) {
                final ConfigurationNode clazz = value.getNode(CLASS_KEY);
                value.setValue(null);
                if (!clazz.isVirtual()) {
                    value.getNode(CLASS_KEY).setValue(clazz);
                }
                return;
            }
            final Class<?> rawType = type.getRawType();
            final ObjectMapper<?> mapper;
            if (rawType.isInterface() || Modifier.isAbstract(rawType.getModifiers())) {
                // serialize obj's concrete type rather than the interface/abstract class
                value.getNode(CLASS_KEY).setValue(obj.getClass().getName());
                mapper = value.getOptions().getObjectMapperFactory().getMapper((obj.getClass()));
            } else {
                // TODO: Fix for 3.7
                mapper = value.getOptions().getObjectMapperFactory().getMapper(type.getRawType());
            }
            ((ObjectMapper<Object>) mapper).bind(obj).serialize(value);
        }
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
