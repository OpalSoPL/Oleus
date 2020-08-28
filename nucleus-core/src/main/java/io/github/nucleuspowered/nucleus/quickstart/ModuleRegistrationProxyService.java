/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.quickstart;

import com.google.common.base.Preconditions;
import io.github.nucleuspowered.nucleus.api.core.exception.ModulesLoadedException;
import io.github.nucleuspowered.nucleus.api.core.exception.NoModuleException;
import io.github.nucleuspowered.nucleus.api.core.exception.UnremovableModuleException;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.apache.logging.log4j.Logger;
import org.spongepowered.plugin.PluginContainer;
import uk.co.drnaylor.quickstart.ModuleHolder;
import uk.co.drnaylor.quickstart.enums.ConstructionPhase;
import uk.co.drnaylor.quickstart.exceptions.QuickStartModuleLoaderException;
import uk.co.drnaylor.quickstart.exceptions.UndisableableModuleException;

import com.google.inject.Inject;

public class ModuleRegistrationProxyService {

    private final INucleusServiceCollection serviceCollection;
    private final ModuleHolder<?, ?> moduleHolder;

    @Inject
    public ModuleRegistrationProxyService(final INucleusServiceCollection serviceCollection, final ModuleHolder<?, ?> holder) {
        this.serviceCollection = serviceCollection;
        this.moduleHolder = holder;
    }

    public boolean canDisableModules() {
        return this.moduleHolder.getCurrentPhase() == ConstructionPhase.DISCOVERED;
    }

    public void removeModule(final String module, final PluginContainer plugin) throws ModulesLoadedException, UnremovableModuleException, NoModuleException {
        if (!this.canDisableModules()) {
            throw new ModulesLoadedException();
        }

        // The plugin must actually be a plugin.
        Preconditions.checkNotNull(plugin);
        final Logger logger = this.serviceCollection.logger();
        final IMessageProviderService messageProviderService = this.serviceCollection.messageProvider();
        try {
            this.moduleHolder.disableModule(module);
            logger.info(messageProviderService.getMessageString("nucleus.module.disabled.modulerequest",
                    plugin.getMetadata().getName(), plugin.getMetadata().getId(), module));
        } catch (final IllegalStateException e) {
            throw new ModulesLoadedException();
        } catch (final UndisableableModuleException e) {
            logger.warn(messageProviderService.getMessageString("nucleus.module.disabled.forceload",
                    plugin.getMetadata().getName(),
                    plugin.getMetadata().getId(),
                    module));
            logger.warn(messageProviderService.getMessageString("nucleus.module.disabled.forceloadtwo", plugin.getMetadata().getName()));
            throw new UnremovableModuleException();
        } catch (final uk.co.drnaylor.quickstart.exceptions.NoModuleException e) {
            throw new NoModuleException();
        } catch (final QuickStartModuleLoaderException e) {
            e.printStackTrace();
        }
    }
}
