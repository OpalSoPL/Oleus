/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public final class Util {

    private Util() {
    }

    public static final DateTimeFormatter FULL_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)
            .withZone(ZoneId.systemDefault());

    public static final Component SPACE = Component.space();

    public static final String usernameRegexPattern = "[0-9a-zA-Z_]{3,16}";
    public static final Pattern usernameRegex = Pattern.compile(usernameRegexPattern);

    public static final UUID CONSOLE_FAKE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static String getNameOrUnkown(final ICommandContext context, final GameProfile profile) {
        return profile.getName().orElse(
                context.getServiceCollection().messageProvider().getMessageString("standard.unknown"));
    }

    public static String getTimeFromTicks(final IMessageProviderService messageProviderService, long ticks) {
        if (ticks < 0 || ticks > 23999) {
            // Normalise
            ticks = ticks % 24000;
        }

        final int mins = (int) ((ticks % 1000) / (100. / 6.));
        long hours = (ticks / 1000 + 6) % 24;

        final NumberFormat m = NumberFormat.getIntegerInstance();
        m.setMinimumIntegerDigits(2);

        if (hours < 12) {
            final long ahours = hours == 0 ? 12 : hours;
            return messageProviderService.getMessageString("standard.time.am", ahours, hours, m.format(mins));
        } else {
            hours -= 12;
            final long ahours = hours == 0 ? 12 : hours;
            return messageProviderService.getMessageString("standard.time.pm", ahours, hours, m.format(mins));
        }
    }

    /**
     * Gets a key from a map based on a case insensitive key.
     *
     * @param map The {@link Map} to check.
     * @param key The {@link String} key.
     * @return An {@link Optional}, which contains the key if it exists.
     */
    public static Optional<String> getKeyIgnoreCase(final Map<String, ?> map, final String key) {
        return getKeyIgnoreCase(map.keySet(), key);
    }

    /**
     * Gets a key from a map based on a case insensitive key.
     *
     * @param collection The {@link Collection} to check.
     * @param key The {@link String} key.
     * @return An {@link Optional}, which contains the key if it exists.
     */
    public static Optional<String> getKeyIgnoreCase(final Collection<String> collection, final String key) {
        return collection.stream().filter(x -> x.equalsIgnoreCase(key)).findFirst();
    }

    /**
     * Gets a value from a map based on a case insensitive key.
     *
     * @param map The {@link Map} to check.
     * @param key The {@link String} key.
     * @param <T> The type of values in the map.
     * @return An {@link Optional}, which contains a value if the key exists in some case form.
     */
    public static <T> Optional<T> getValueIgnoreCase(final Map<String, T> map, final String key) {
        return map.entrySet().stream().filter(x -> x.getKey().equalsIgnoreCase(key))
                .map(Map.Entry::getValue).findFirst();
    }

    /**
     * Tests to see if the supplied {@link Location} is within the world's {@link org.spongepowered.api.world.WorldBorder}
     *
     * @param location The {@link Location} to test.
     * @return <code>true</code> if the location is within the border.
     */
    public static boolean isLocationInWorldBorder(final ServerLocation location) {
        return isLocationInWorldBorder(location.getPosition(), location.getWorld());
    }

    public static boolean isLocationInWorldBorder(final Vector3d location, final ServerWorld world) {

        // Diameter, not radius - we'll want the radius later. We use long, we want the floor!
        final long radius = (long)Math.floor(world.getProperties().getWorldBorder().getDiameter() / 2.0);

        // We get the current position and subtract the border centre. This gives us an effective distance from the
        // centre in all three dimensions. We just care about the magnitude in the x and z directions, so we get the
        // positive amount.
        final Vector3d displacement = location.sub(world.getProperties().getWorldBorder().getCenter()).abs();

        // Check that we're not too far out.
        return !(displacement.getX() > radius || displacement.getZ() > radius);
    }

    public static void compressAndDeleteFile(final Path from) throws IOException {
        // Get the file.
        if (Files.exists(from)) {
            final Path to = Paths.get(from.toString() + ".gz");
            try (final OutputStream os = new GZIPOutputStream(new FileOutputStream(to.toFile()))) {
                Files.copy(from, os);
                os.flush();
                Files.delete(from);
            }

        }

    }

    public static PaginationList.Builder getPaginationBuilder(final Audience source) {
        return getPaginationBuilder(source instanceof ServerPlayer);
    }

    public static PaginationList.Builder getPaginationBuilder(final boolean isPlayer) {
        final PaginationList.Builder plb = Sponge.getServiceProvider().paginationService().builder();
        if (!isPlayer) {
            plb.linesPerPage(-1);
        }

        return plb;
    }

    public static Inventory.Builder.EndStep getKitInventoryBuilder() {
        return Inventory.builder().grid(9, 4).completeStructure();
    }

    public static ItemStack dropItemOnFloorAtLocation(final ItemStackSnapshot itemStackSnapshotToDrop, final ServerLocation location) {
        return dropItemOnFloorAtLocation(itemStackSnapshotToDrop, location.getWorld(), location.getPosition());
    }

    public static ItemStack dropItemOnFloorAtLocation(final ItemStackSnapshot itemStackSnapshotToDrop, final ServerWorld world,
            final Vector3d position) {
        final Entity entityToDrop = world.createEntity(EntityTypes.ITEM.get(), position);
        entityToDrop.offer(Keys.ITEM_STACK_SNAPSHOT, itemStackSnapshotToDrop);
        world.spawnEntity(entityToDrop);
        return itemStackSnapshotToDrop.createStack();
    }

    public static Inventory getStandardInventory(final Carrier player) {
        return player.getInventory()
                .query(QueryTypes.PLAYER_PRIMARY_HOTBAR_FIRST.get().toQuery());
    }


}
