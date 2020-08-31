/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.storage.persistence;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.nucleuspowered.nucleus.util.ThrownFunction;
import io.github.nucleuspowered.storage.exceptions.DataDeleteException;
import io.github.nucleuspowered.storage.exceptions.DataLoadException;
import io.github.nucleuspowered.storage.exceptions.DataQueryException;
import io.github.nucleuspowered.storage.exceptions.DataSaveException;
import io.github.nucleuspowered.storage.persistence.IStorageRepository;
import io.github.nucleuspowered.storage.queryobjects.IQueryObject;
import io.github.nucleuspowered.storage.util.KeyedObject;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

abstract class FlatFileStorageRepository implements IStorageRepository {

    private final Logger logger;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected FlatFileStorageRepository(final Logger logger) {
        this.logger = logger;
    }

    Optional<JsonObject> get(@Nullable final Path path) throws DataLoadException {
        if (path != null) {
            try {
                if (Files.size(path) == 0) {
                    return Optional.empty(); // nothing in the file, don't do anything with it.
                }
                // Read the file.
                try (final BufferedReader reader = Files.newBufferedReader(path)) {
                    return Optional.of(new JsonParser()
                            .parse(reader.lines().collect(Collectors.joining()))
                            .getAsJsonObject());
                }
            } catch (final Exception e) {
                throw new DataLoadException("Could not load file at " + path.toAbsolutePath().toString(), e);
            }
        }

        return Optional.empty();
    }

    void save(final Path file, final JsonObject object) throws DataSaveException {
        try {
            // Backup the file
            if (Files.exists(file)) {
                Files.copy(file, file.resolveSibling(file.getFileName() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            }

            // Write the new file
            Files.createDirectories(file.getParent());
            try (final BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write(gson.toJson(object));
            }
        } catch (final Exception ex) {
            this.logger.error("Could not save " + file.toString());
            ex.printStackTrace();
            throw new DataSaveException("Could not save " + file.toString(), ex);
        }
    }

    @Override
    public void shutdown() {
        // nothing to do
    }

    @Override public void clearCache() {
        // noop
    }

    @Override public boolean hasCache() {
        return false;
    }

    static class Single extends FlatFileStorageRepository implements IStorageRepository.Single<JsonObject> {

        private final Supplier<Path> FILENAME_RESOLVER;

        Single(final Logger logger, final Supplier<Path> filename_resolver) {
            super(logger);
            this.FILENAME_RESOLVER = filename_resolver;
        }

        @Override
        public Optional<JsonObject> get() throws DataLoadException {
            if (Files.exists(this.FILENAME_RESOLVER.get())) {
                return this.get(this.FILENAME_RESOLVER.get());
            }

            return Optional.empty();
        }

        @Override
        public void save(final JsonObject object) throws DataSaveException {
            this.save(this.FILENAME_RESOLVER.get(), object);
        }

    }

    static class UUIDKeyed<Q extends IQueryObject<UUID, Q>>
            extends FlatFileStorageRepository
            implements Keyed<UUID, Q, JsonObject> {

        private final ThrownFunction<Q, Path, DataQueryException> FILENAME_RESOLVER;
        private final Supplier<Path> BASE_PATH;
        private final Function<UUID, Path> UUID_FILENAME_RESOLVER;

        UUIDKeyed(
                final Logger logger,
                final ThrownFunction<Q, Path, DataQueryException> filename_resolver,
                final Function<UUID, Path> uuid_filename_resolver,
                final Supplier<Path> basePath) {
            super(logger);
            this.FILENAME_RESOLVER = filename_resolver;
            this.UUID_FILENAME_RESOLVER = uuid_filename_resolver;
            this.BASE_PATH = basePath;
        }

        @Override
        public boolean exists(final Q query) {
            try {
                return this.existsInternal(query) != null;
            } catch (final DataQueryException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public Optional<KeyedObject<UUID, JsonObject>> get(final Q query) throws DataLoadException {
            final Path path;
            try {
                path = this.existsInternal(query);
            } catch (final Exception e) {
                throw new DataLoadException("Query not valid", e);
            }

            return this.get(path).map(x -> new KeyedObject<>(query.keys().iterator().next(), x));
        }

        @Override
        public boolean exists(final UUID uuid) {
            return this.existsInternal(uuid) != null;
        }

        @Override
        public Optional<JsonObject> get(final UUID uuid) throws DataLoadException {
            return this.get(this.existsInternal(uuid));
        }

        @Override
        public Collection<UUID> getAllKeys() throws DataLoadException {
            return ImmutableSet.copyOf(this.getAllKeysInternal());
        }

        @Override
        public Map<UUID, JsonObject> getAll(final Q query) throws DataLoadException, DataQueryException {
            final ImmutableMap.Builder<UUID, JsonObject> j = ImmutableMap.builder();
            for (final UUID key : this.getAllKeys(query)) {
                j.put(key, this.get(key).get()); // should be there
            }

            return j.build();
        }

        @Override
        public Collection<UUID> getAllKeys(final Q query) throws DataLoadException, DataQueryException {
            if (query.restrictedToKeys()) {
                this.getAllKeysInternal().retainAll(query.keys());
            }

            throw new DataQueryException("There must only a key", query);
        }

        private Set<UUID> getAllKeysInternal() throws DataLoadException {
            final UUIDFileWalker u = new UUIDFileWalker();
            try {
                Files.walkFileTree(this.BASE_PATH.get(), u);
                return u.uuidSet;
            } catch (final IOException e) {
                throw new DataLoadException("Could not walk the file tree", e);
            }
        }

        @Nullable
        private Path existsInternal(final UUID uuid) {
            final Path path = this.UUID_FILENAME_RESOLVER.apply(uuid);
            if (Files.exists(this.UUID_FILENAME_RESOLVER.apply(uuid))) {
                return path;
            }

            return null;
        }


        @Override
        public int count(final Q query) {
            return this.exists(query) ? 1 : 0;
        }

        @Override
        public void save(final UUID key, final JsonObject object) throws DataSaveException {
            final Path file = this.UUID_FILENAME_RESOLVER.apply(key);
            this.save(file, object);
        }

        @Override
        public void delete(final UUID key) throws DataDeleteException {
            final Path filename = this.UUID_FILENAME_RESOLVER.apply(key);

            try {
                Files.delete(filename);
            } catch (final IOException e) {
                throw new DataDeleteException("Could not delete " + filename, e);
            }
        }

        @Nullable
        private Path existsInternal(final Q query) throws DataQueryException {
            final Path path = this.FILENAME_RESOLVER.apply(query);
            if (Files.exists(this.FILENAME_RESOLVER.apply(query))) {
                return path;
            }

            return null;
        }

        private static class UUIDFileWalker extends SimpleFileVisitor<Path> {

            private final Set<UUID> uuidSet = new HashSet<>();

            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                if (dir.getFileName().toString().length() == 2) {
                    return super.preVisitDirectory(dir, attrs);
                }

                return FileVisitResult.SKIP_SUBTREE;
            }

            // Print information about
            // each type of file.
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attr) {
                if (attr.isRegularFile()) {
                    if (file.endsWith(".json")) {
                        final String f = file.getFileName().toString();
                        if (f.length() == 41 && f.startsWith(file.getParent().toString().toLowerCase())) {
                            try {
                                this.uuidSet.add(UUID.fromString(f.substring(0, 36)));
                            } catch (final Exception e) {
                                // ignored
                            }
                        }
                    }
                }

                return FileVisitResult.CONTINUE;
            }
        }

    }

}
