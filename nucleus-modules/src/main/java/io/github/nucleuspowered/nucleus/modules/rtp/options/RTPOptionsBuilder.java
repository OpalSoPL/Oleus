/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rtp.options;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.api.module.rtp.NucleusRTPService;
import org.spongepowered.api.world.biome.Biome;

import java.util.HashSet;
import java.util.Set;

public class RTPOptionsBuilder implements NucleusRTPService.RTPOptions.Builder {

    int max = 30000;
    int min = 0;
    int minheight = 1;
    int maxheight = 255;
    final Set<Biome> prohibitedBiomes = new HashSet<>();

    @Override public NucleusRTPService.RTPOptions.Builder setMaxRadius(final int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("Max must be positive");
        }
        this.max = max;
        return this;
    }

    @Override public NucleusRTPService.RTPOptions.Builder setMinRadius(final int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Min cannot be negative");
        }
        this.min = min;
        return this;
    }

    @Override public NucleusRTPService.RTPOptions.Builder setMinHeight(final int min) throws IllegalArgumentException {
        if (min < 0) {
            throw new IllegalArgumentException("Min must be non-negative");
        }
        this.minheight = min;
        return this;
    }

    @Override public NucleusRTPService.RTPOptions.Builder setMaxHeight(final int max) throws IllegalArgumentException {
        if (max > 255) {
            throw new IllegalArgumentException("Max must be less than 255");
        }
        this.maxheight = max;
        return this;
    }

    @Override public NucleusRTPService.RTPOptions.Builder prohibitedBiome(final Biome biomeType) {
        this.prohibitedBiomes.add(Preconditions.checkNotNull(biomeType));
        return this;
    }

    @Override public NucleusRTPService.RTPOptions.Builder from(final NucleusRTPService.RTPOptions options) {
        return this.setMinRadius(options.minRadius())
                .setMaxRadius(options.maxRadius())
                .setMaxHeight(options.maxHeight())
                .setMinHeight(options.minHeight());
    }

    @Override public NucleusRTPService.RTPOptions build() {
        if (this.min >= this.max) {
            throw new IllegalStateException("Minimum is bigger than maximum");
        }
        if (this.minheight >= this.maxheight) {
            throw new IllegalStateException("Minimum height is bigger than maximum height");
        }
        return new RTPOptions(this);
    }

    @Override public NucleusRTPService.RTPOptions.Builder reset() {
        this.max = 30000;
        this.min = 0;
        this.minheight = 1;
        this.maxheight = 255;
        this.prohibitedBiomes.clear();
        return this;
    }
}
