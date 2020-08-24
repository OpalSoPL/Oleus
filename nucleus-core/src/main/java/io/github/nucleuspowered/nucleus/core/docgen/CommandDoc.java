/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.docgen;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class CommandDoc {

    @Setting
    private String commandName;

    @Setting
    private String aliases;

    @Setting
    private String rootAliases;

    @Setting
    private String defaultLevel;

    @Setting
    private String usageString;

    @Setting
    private String oneLineDescription;

    @Setting
    private String extendedDescription;

    @Setting
    private String module;

    @Setting
    private String permissionbase;

    @Setting
    private boolean warmup;

    @Setting
    private boolean cooldown;

    @Setting
    private boolean cost;

    @Setting
    private List<String> essentialsEquivalents;

    @Setting
    private Boolean isExactEssEquiv = null;

    @Setting
    private String essNotes;

    @Setting
    private List<PermissionDoc> permissions;

    @Setting private String simpleUsage;

    @Setting private String subcommands;

    @Setting private String context;

    public void setCommandName(final String commandName) {
        this.commandName = commandName;
    }

    public String getAliases() {
        return this.aliases;
    }

    public void setAliases(final String aliases) {
        this.aliases = aliases;
    }

    public void setDefaultLevel(final String defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public void setUsageString(final String usageString) {
        this.usageString = usageString;
    }

    public void setOneLineDescription(final String oneLineDescription) {
        this.oneLineDescription = oneLineDescription;
    }

    public void setExtendedDescription(final String extendedDescription) {
        this.extendedDescription = extendedDescription;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(final String module) {
        this.module = module;
    }

    public void setPermissionbase(final String permissionbase) {
        this.permissionbase = permissionbase.replaceAll("\\.base", "");
    }

    public boolean isWarmup() {
        return this.warmup;
    }

    public void setWarmup(final boolean warmup) {
        this.warmup = warmup;
    }

    public boolean isCooldown() {
        return this.cooldown;
    }

    public void setCooldown(final boolean cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isCost() {
        return this.cost;
    }

    public void setCost(final boolean cost) {
        this.cost = cost;
    }

    public List<PermissionDoc> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(final List<PermissionDoc> permissions) {
        this.permissions = permissions;
    }

    public void setRootAliases(final String rootAliases) {
        this.rootAliases = rootAliases;
    }

    public String getSubcommands() {
        return this.subcommands;
    }

    public void setSubcommands(final String subcommands) {
        this.subcommands = subcommands;
    }

    public void setEssentialsEquivalents(final List<String> essentialsEquivalents) {
        this.essentialsEquivalents = essentialsEquivalents;
    }

    public void setExactEssEquiv(final Boolean exactEssEquiv) {
        this.isExactEssEquiv = exactEssEquiv;
    }

    public void setEssNotes(final String essNotes) {
        this.essNotes = essNotes;
    }

    public void setSimpleUsage(final String simpleUsage) {
        this.simpleUsage = simpleUsage;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(final String context) {
        this.context = context;
    }
}
