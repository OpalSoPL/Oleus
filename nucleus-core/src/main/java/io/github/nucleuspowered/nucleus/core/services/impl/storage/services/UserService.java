/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.storage.services;

import com.google.gson.JsonObject;
import io.github.nucleuspowered.nucleus.core.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.core.services.impl.storage.queryobjects.IUserQueryObject;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IDataVersioning;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IStorageManager;
import io.github.nucleuspowered.storage.services.AbstractKeyedService;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.UUID;
import java.util.function.BiConsumer;

public final class UserService extends AbstractKeyedService<UUID, IUserQueryObject, IUserDataObject, JsonObject> {

    public UserService(final IStorageManager repository, final PluginContainer pluginContainer, final IDataVersioning dataVersioning) {
        super(repository::getUserDataAccess, repository::getUserRepository, dataVersioning::migrate, dataVersioning::setVersion, pluginContainer);
    }

    @Override
    protected void onEviction(final UUID key, final IUserDataObject dataObject, final BiConsumer<UUID, IUserDataObject> reAdd) {
        Sponge.server().getPlayer(key).ifPresent(x -> reAdd.accept(key, dataObject));
    }

}
