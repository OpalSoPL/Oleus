/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.configurate.typeserialisers;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.configurate.wrappers.NucleusItemStackSnapshot;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.util.PrettyPrinter;
import io.github.nucleuspowered.nucleus.util.TypeHelper;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NucleusItemStackSnapshotSerialiser implements TypeSerializer<NucleusItemStackSnapshot> {

    private final Logger logger;

    public NucleusItemStackSnapshotSerialiser(final INucleusServiceCollection serviceCollection) {
        this.logger = serviceCollection.logger();
    }

    @Override
    public NucleusItemStackSnapshot deserialize(final TypeToken<?> type, final ConfigurationNode value) {
        // Process enchantments, temporary fix before Sponge gets a more general fix in.
        boolean emptyEnchant = false;
        final ConfigurationNode ench = value.getNode("UnsafeData", "ench");
        if (!ench.isVirtual()) {
            final List<? extends ConfigurationNode> enchantments = ench.getChildrenList();
            if (enchantments.isEmpty()) {
                // Remove empty enchantment list.
                value.getNode("UnsafeData").removeChild("ench");
            } else {
                enchantments.forEach(x -> {
                    try {
                        final short id = Short.parseShort(x.getNode("id").getString());
                        final short lvl = Short.parseShort(x.getNode("lvl").getString());

                        x.getNode("id").setValue(id);
                        x.getNode("lvl").setValue(lvl);
                    } catch (final NumberFormatException e) {
                        x.setValue(null);
                    }
                });
            }
        }

        final ConfigurationNode data = value.getNode("Data");
        if (!data.isVirtual() && data.hasListChildren()) {
            final List<? extends ConfigurationNode> n = data.getChildrenList().stream()
                .filter(x ->
                        !x.getNode("DataClass").getString("").endsWith("SpongeEnchantmentData")
                    || (!x.getNode("ManipulatorData", "ItemEnchantments").isVirtual() && x.getNode("ManipulatorData", "ItemEnchantments").hasListChildren()))
                .collect(Collectors.toList());
            emptyEnchant = n.size() != data.getChildrenList().size();

            if (emptyEnchant) {
                if (n.isEmpty()) {
                    value.removeChild("Data");
                } else {
                    value.getNode("Data").setValue(n);
                }
            }
        }

        final DataContainer dataContainer = DataTranslators.CONFIGURATION_NODE.translate(value);
        final Set<DataQuery> ldq = dataContainer.getKeys(true);

        for (final DataQuery dataQuery : ldq) {
            final String el = dataQuery.asString(".");
            if (el.contains("$Array$")) {
                try {
                    final Tuple<DataQuery, Object> r = TypeHelper.getArray(dataQuery, dataContainer);
                    dataContainer.set(r.getFirst(), r.getSecond());
                } catch (final Exception e) {
                    e.printStackTrace();
                }

                dataContainer.remove(dataQuery);
            }
        }

        final ItemStack snapshot;
        try {
            snapshot = ItemStack.builder().fromContainer(dataContainer).build();
        } catch (final Exception e) {
            return NucleusItemStackSnapshot.NONE;
        }

        // Validate the item.
        if (snapshot.isEmpty() || snapshot.getType() == ItemTypes.NONE) {
            // don't bother
            return NucleusItemStackSnapshot.NONE;
        }

        if (snapshot.getType() == null) {
            // well, this isn't going to work, is it?
            final PrettyPrinter printer = new PrettyPrinter();
            printer.add("Null item type recorded when trying to create an item stack").centre().hr();
            printer.add("When trying to load item stacks for kits, a null item type was found. This can occur when changing packs and items no "
                    + "longer exist. The item has been discarded.");
            printer.add("Item Info:");
            try {
                printer.add(DataFormats.JSON.write(dataContainer));
            } catch (final IOException e) {
                printer.add("Unable to write info");
            }
            printer.log(this.logger, Level.WARN);
            return NucleusItemStackSnapshot.NONE;
        }

        if (emptyEnchant) {
            snapshot.offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList());
            return new NucleusItemStackSnapshot(snapshot.createSnapshot());
        }

        if (snapshot.get(Keys.ITEM_ENCHANTMENTS).isPresent()) {
            // Reset the data.
            snapshot.offer(Keys.ITEM_ENCHANTMENTS, snapshot.get(Keys.ITEM_ENCHANTMENTS).get());
            return new NucleusItemStackSnapshot(snapshot.createSnapshot());
        }

        return new NucleusItemStackSnapshot(snapshot.createSnapshot());
    }

    @Override
    public void serialize(final TypeToken<?> type, final NucleusItemStackSnapshot obj, final ConfigurationNode value) throws ObjectMappingException {
        final ItemStackSnapshot snapshot = obj.getSnapshot();
        final DataView view;
        try {
             view = snapshot.toContainer();
        } catch (final NullPointerException ex) {
            final PrettyPrinter printer = new PrettyPrinter();
            // Sponge can't find an item type...
            printer.add("NPE encountered when trying to save an item to a kit").centre().hr();
            printer.add("When trying to save an item to the kit, Sponge could not turn the item into data Nucleus can save.");
            printer.add("This can occur when changing packs and items no longer exist.");
            printer.hr();
            final ItemType itemType = snapshot.getType();
            if (itemType == null) {
                printer.add("The ItemType was set to null, but is not class as an empty snapshot. It will not be saved.");
            } else {
                printer.add("The ItemType was set to {} (name: {}). It will not be saved.", itemType.getId(), itemType.getName());
            }

            printer.hr();
            printer.add("Stack trace:");
            printer.add(ex);
            printer.log(this.logger, Level.WARN);
            return;
        }
        final Map<DataQuery, Object> dataQueryObjectMap = view.getValues(true);
        for (final Map.Entry<DataQuery, Object> entry : dataQueryObjectMap.entrySet()) {
            if (entry.getValue().getClass().isArray()) {
                // Convert to a list with type, make it the key.
                if (entry.getValue().getClass().getComponentType().isPrimitive()) {
                    // Create the list of the primitive type.
                    final DataQuery old = entry.getKey();
                    final Tuple<DataQuery, List<?>> dqo = TypeHelper.getList(old, entry.getValue());
                    view.remove(old);
                    view.set(dqo.getFirst(), dqo.getSecond());
                } else {
                    // create a list type
                    view.set(entry.getKey(), Lists.newArrayList((Object[]) entry.getValue()));
                }
            }
        }

        value.setValue(DataTranslators.CONFIGURATION_NODE.translate(view));
    }

}
