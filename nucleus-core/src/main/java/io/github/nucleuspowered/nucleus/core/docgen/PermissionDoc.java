/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.docgen;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Objects;

@ConfigSerializable
public class PermissionDoc {

    public PermissionDoc() { }

    public PermissionDoc(final String permission, final String description, final String defaultLevel, final String module) {
        this.permission = permission;
        this.description = description;
        this.defaultLevel = defaultLevel;
        this.module = module;
    }

    @Setting
    private String permission;

    @Setting
    private String description;

    @Setting
    private String defaultLevel;

    @Setting
    private String module;

    public String getPermission() {
        return this.permission;
    }

    public PermissionDoc setPermission(final String permission) {
        this.permission = permission;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public PermissionDoc setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getModule() {
        return this.module;
    }

    public PermissionDoc setModule(final String module) {
        this.module = module;
        return this;
    }

    public String getDefaultLevel() {
        return this.defaultLevel;
    }

    public PermissionDoc setDefaultLevel(final String defaultLevel) {
        this.defaultLevel = defaultLevel;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final PermissionDoc that = (PermissionDoc) o;
        return Objects.equals(this.permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.permission);
    }

}
