/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.docgen;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.List;

@ConfigSerializable
public class EssentialsDoc {

    @Setting
    private List<String> essentialsCommands;

    @Setting
    private List<String> nucleusEquiv;

    @Setting
    private boolean isExact;

    @Setting
    private String notes;

    public List<String> getEssentialsCommands() {
        return this.essentialsCommands;
    }

    public EssentialsDoc setEssentialsCommands(final List<String> essentialsCommands) {
        this.essentialsCommands = essentialsCommands;
        return this;
    }

    public List<String> getNucleusEquiv() {
        return this.nucleusEquiv;
    }

    public EssentialsDoc setNucleusEquiv(final List<String> nucleusEquiv) {
        this.nucleusEquiv = nucleusEquiv;
        return this;
    }

    public boolean isExact() {
        return this.isExact;
    }

    public EssentialsDoc setExact(final boolean exact) {
        this.isExact = exact;
        return this;
    }

    public String getNotes() {
        return this.notes;
    }

    public EssentialsDoc setNotes(final String notes) {
        this.notes = notes;
        return this;
    }
}
