/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.storage.services;

import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.nucleus.services.impl.storage.queryobjects.IUserQueryObject;
import io.github.nucleuspowered.nucleus.services.interfaces.IDataVersioning;
import io.github.nucleuspowered.nucleus.services.interfaces.IStorageManager;
import io.github.nucleuspowered.storage.services.AbstractKeyedService;
import org.spongepowered.plugin.PluginContainer;

import java.util.UUID;

public class UserService extends AbstractKeyedService<UUID, IUserQueryObject, IUserDataObject> {

    public UserService(final IStorageManager repository, final PluginContainer pluginContainer, final IDataVersioning dataVersioning) {
        super(repository::getUserDataAccess, repository::getUserRepository, dataVersioning::migrate, dataVersioning::setVersion, pluginContainer);
    }
}
