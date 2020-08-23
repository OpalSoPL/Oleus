package io.github.nucleuspowered.nucleus.module;

public class ModuleContainer {

    private final String id;
    private final boolean isRequired;
    private final Class<? extends IModule> moduleClass;

    public ModuleContainer(final String id,
            final boolean isRequired,
            final Class<? extends IModule> moduleClass) {
        this.id = id;
        this.isRequired = isRequired;
        this.moduleClass = moduleClass;
    }

    public String getId() {
        return this.id;
    }

    public boolean isRequired() {
        return this.isRequired;
    }

    public Class<? extends IModule> getModuleClass() {
        return this.moduleClass;
    }

    public static final class Configurable<T> extends ModuleContainer {

        private final Class<T> configurationClass;

        public Configurable(
                final String id,
                final boolean isRequired,
                final Class<? extends IModule> moduleClass,
                final Class<T> configurationClass) {
            super(id, isRequired, moduleClass);
            this.configurationClass = configurationClass;
        }

        public Class<T> getConfigurationClass() {
            return this.configurationClass;
        }
    }

}
