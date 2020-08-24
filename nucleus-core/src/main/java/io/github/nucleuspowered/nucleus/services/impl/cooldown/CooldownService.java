/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.cooldown;

import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.services.interfaces.ICooldownService;
import org.spongepowered.api.util.Identifiable;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class CooldownService implements ICooldownService {

    private final Map<DualKey, Instant> cooldowns = new HashMap<>();

    private Map<DualKey, Instant> cleanUp() {
        final Instant now = Instant.now();
        final Collection<DualKey> keys = this.cooldowns.entrySet()
                .stream()
                .filter(x -> x.getValue().isBefore(now))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        for (final DualKey key : keys) {
            this.cooldowns.remove(key);
        }

        return this.cooldowns;
    }

    @Override public boolean hasCooldown(final String key, final Identifiable identifiable) {
        return this.cleanUp().containsKey(new DualKey(key, identifiable.getUniqueId()));
    }

    @Override public Optional<Duration> getCooldown(final String key, final Identifiable identifiable) {
        return Optional.ofNullable(this.cleanUp()
                .get(new DualKey(key, identifiable.getUniqueId())))
                .map(x -> Duration.between(Instant.now(), x));
    }

    @Override public void setCooldown(final String key, final Identifiable identifiable, final Duration cooldownLength) {
        this.cooldowns.put(new DualKey(key, identifiable.getUniqueId()), Instant.now().plus(cooldownLength));
    }

    @Override public void clearCooldown(final String key, final Identifiable identifiable) {
        this.cooldowns.remove(new DualKey(key, identifiable.getUniqueId()));
    }

    private final static class DualKey {

        private final String key;
        private final UUID uuid;

        private DualKey(final String key, final UUID uuid) {
            this.key = key;
            this.uuid = uuid;
        }

        @Override public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final DualKey dualKey = (DualKey) o;
            return Objects.equals(this.key, dualKey.key) &&
                    Objects.equals(this.uuid, dualKey.uuid);
        }

        @Override public int hashCode() {
            return Objects.hash(this.key, this.uuid.getLeastSignificantBits(), this.uuid.getMostSignificantBits());
        }
    }
}
