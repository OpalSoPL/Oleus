/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.home.services;

import io.github.nucleuspowered.nucleus.api.util.data.NamedLocation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.persistence.Queries;
import io.github.nucleuspowered.nucleus.api.module.home.data.Home;
import io.github.nucleuspowered.nucleus.core.datatypes.NucleusNamedLocation;

import java.util.Optional;
import java.util.UUID;

public class NucleusHome implements Home {

    private final static int CONTENT_VERSION = 1;

    private final static DataQuery OWNER = DataQuery.of("owner");

    private final UUID owner;
    private final NamedLocation namedLocation;

    public NucleusHome(final UUID owner, final NamedLocation location) {
        this.owner = owner;
        this.namedLocation = location;
    }

    @Override
    public UUID getOwnersUniqueId() {
        return this.owner;
    }

    @Override
    public NamedLocation getLocation() {
        return this.namedLocation;
    }

    @Override
    public int contentVersion() {
        return NucleusHome.CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew()
                .set(Queries.CONTENT_VERSION, this.contentVersion())
                .set(NucleusHome.OWNER, this.owner)
                .set(NucleusNamedLocation.NAMED_LOCATION_DATA_QUERY, this.namedLocation);
    }

    public static final class DataBuilder extends AbstractDataBuilder<Home> {

        public DataBuilder() {
            super(Home.class, NucleusHome.CONTENT_VERSION);
        }

        @Override
        protected Optional<Home> buildContent(final DataView container) throws InvalidDataException {
            if (!container.contains(Queries.CONTENT_VERSION)) {
                NucleusNamedLocation.upgradeLegacy(container, NucleusNamedLocation.NAMED_LOCATION_DATA_QUERY);
            }

            final DataTranslator<UUID> translator = Sponge.dataManager().translator(UUID.class).get();
            return Optional.of(new NucleusHome(
                    container.getView(NucleusHome.OWNER).map(translator::translate).get(),
                    container.getSerializable(NucleusNamedLocation.NAMED_LOCATION_DATA_QUERY, NamedLocation.class).get()
            ));
        }
    }
}
