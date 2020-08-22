package io.github.nucleuspowered.nucleus.module;

import java.util.function.Supplier;

public final class ModuleContainer {

    private final String id;
    private final String name;
    private final boolean isRequired;
    private final Supplier<? extends IModule> moduleConstructor;

    public ModuleContainer(final String id,
            final String name,
            final boolean isRequired,
            final Supplier<? extends IModule> moduleConstructor) {
        this.id = id;
        this.name = name;
        this.isRequired = isRequired;
        this.moduleConstructor = moduleConstructor;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isRequired() {
        return this.isRequired;
    }

    public Supplier<? extends IModule> getModuleConstructor() {
        return this.moduleConstructor;
    }

}
