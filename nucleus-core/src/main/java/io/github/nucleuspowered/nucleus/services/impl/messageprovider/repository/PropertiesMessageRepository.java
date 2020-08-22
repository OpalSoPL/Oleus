/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.messageprovider.repository;

import io.github.nucleuspowered.nucleus.services.interfaces.IPlayerDisplayNameService;
import io.github.nucleuspowered.nucleus.services.interfaces.ITextStyleService;

import java.util.Collection;
import java.util.ResourceBundle;

public class PropertiesMessageRepository extends AbstractMessageRepository implements IMessageRepository {

    private final ResourceBundle resource;

    public PropertiesMessageRepository(final ITextStyleService textStyleService,
            final IPlayerDisplayNameService playerDisplayNameService,
            final ResourceBundle resource) {
        super(textStyleService, playerDisplayNameService);
        this.resource = resource;
    }

    public Collection<String> getKeys() {
        return this.resource.keySet();
    }

    @Override
    public boolean hasEntry(final String key) {
        return this.resource.containsKey(key);
    }

    @Override
    String getEntry(final String key) {
        if (this.resource.containsKey(key)) {
            return this.resource.getString(key);
        }

        throw new IllegalArgumentException("The key " + key + " does not exist!");
    }

}
