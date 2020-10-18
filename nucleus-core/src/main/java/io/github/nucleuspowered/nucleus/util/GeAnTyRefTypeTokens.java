package io.github.nucleuspowered.nucleus.util;

import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.configurate.AbstractConfigurateBackedDataObject;
import io.leangen.geantyref.TypeToken;
import org.spongepowered.api.ResourceKey;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public final class GeAnTyRefTypeTokens {

    public static final TypeToken<AbstractConfigurateBackedDataObject> ABSTRACT_DATA_OBJECT_TYPE_TOKEN =
            TypeToken.get(AbstractConfigurateBackedDataObject.class);

    public static final TypeToken<Boolean> BOOLEAN = TypeToken.get(Boolean.class);

    public static final TypeToken<Instant> INSTANT = TypeToken.get(Instant.class);

    public static final TypeToken<Integer> INTEGER = TypeToken.get(Integer.class);

    public static final TypeToken<Locale> LOCALE = TypeToken.get(Locale.class);

    public static final TypeToken<NamedLocation> NAMED_LOCATION = TypeToken.get(NamedLocation.class);

    public static final TypeToken<ResourceKey> RESOURCE_KEY = TypeToken.get(ResourceKey.class);

    public static final TypeToken<String> STRING = TypeToken.get(String.class);

    public static final TypeToken<UUID> UUID = TypeToken.get(UUID.class);

    private GeAnTyRefTypeTokens() {
        // no-op
    }

}