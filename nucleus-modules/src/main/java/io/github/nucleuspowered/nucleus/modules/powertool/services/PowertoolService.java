/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.powertool.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.nucleuspowered.nucleus.modules.powertool.PowertoolKeys;
import io.github.nucleuspowered.nucleus.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IStorageManager;
import org.spongepowered.api.item.ItemType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.google.inject.Inject;

public class PowertoolService implements ServiceBase {

    private final Map<UUID, Map<String, List<String>>> powertools = new HashMap<>();

    private final IStorageManager storageManager;

    @Inject
    public PowertoolService(final INucleusServiceCollection serviceCollection) {
        this.storageManager = serviceCollection.storageManager();
    }


    public Map<String, List<String>> getPowertools(final UUID uuid) {
        Map<String, List<String>> m = this.powertools.get(uuid);
        if (m == null) {
            // grab the user data
            m = this.storageManager.getUserService()
                    .getOrNewOnThread(uuid)
                    .get(PowertoolKeys.POWERTOOLS)
                    .orElseGet(HashMap::new);
            this.powertools.put(uuid, m);
        }

        return ImmutableMap.copyOf(m);
    }

    public Optional<List<String>> getPowertoolForItem(final UUID uuid, final ItemType item) {
        final List<String> tools = this.getPowertools(uuid).get(item.getKey().asString());
        if (tools != null) {
            return Optional.of(ImmutableList.copyOf(tools));
        }

        return Optional.empty();
    }

    public void setPowertool(final UUID uuid, final ItemType type, final List<String> commands) {
        this.getPowertools(uuid).put(type.getKey().asString(), new ArrayList<>(commands));
        this.setBack(uuid);
    }

    public void clearPowertool(final UUID uuid, final ItemType type) {
        this.clearPowertool(uuid, type.getKey().asString());
    }

    public void clearPowertool(final UUID uuid, final String type) {
        this.getPowertools(uuid).remove(type);
        this.setBack(uuid);
    }

    public void reset(final UUID uuid) {
        this.powertools.remove(uuid);
        this.setBack(uuid);
    }

    private void setBack(final UUID uuid) {
        this.storageManager
                .getUserService()
                .getOrNew(uuid)
                .thenAccept(x -> x.set(PowertoolKeys.POWERTOOLS, this.powertools.getOrDefault(uuid, new HashMap<>())));
    }
}
