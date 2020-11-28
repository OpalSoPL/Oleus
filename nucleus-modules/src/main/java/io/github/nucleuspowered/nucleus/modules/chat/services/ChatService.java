/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.chat.services;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import io.github.nucleuspowered.nucleus.core.services.impl.texttemplatefactory.NucleusTextTemplateImpl;
import io.github.nucleuspowered.nucleus.core.services.interfaces.INucleusTextTemplateFactory;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatConfig;
import io.github.nucleuspowered.nucleus.modules.chat.config.ChatTemplateConfig;
import io.github.nucleuspowered.nucleus.core.scaffold.service.ServiceBase;
import io.github.nucleuspowered.nucleus.core.services.INucleusServiceCollection;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IReloadableService;
import org.spongepowered.api.service.permission.Subject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Contains the logic for caching templates and the template selection logic.
 */
public class ChatService implements IReloadableService.Reloadable, ServiceBase {

    private final INucleusTextTemplateFactory textTemplateFactory;
    private ChatConfig config = new ChatConfig();
    private final Map<String, TemplateCache> templateCacheMap = new HashMap<>();
    private TemplateCache defaultTemplate =
            new TemplateCache(new ChatTemplateConfig(), NucleusTextTemplateImpl.empty(), NucleusTextTemplateImpl.empty());

    @Inject
    public ChatService(final INucleusTextTemplateFactory textTemplateFactory) {
        this.textTemplateFactory = textTemplateFactory;
    }

    public TemplateCache getTemplateNow(final Subject subject) {
        if (!this.config.isUseGroupTemplates()) {
            return this.defaultTemplate;
        }

        return subject.getOption("nucleus.chat.group")
                .map(this::getTemplateCache)
                .orElse(this.defaultTemplate);
    }

    private TemplateCache getTemplateCache(final String cache) {
        return this.templateCacheMap.computeIfAbsent(cache, key -> {
            final ChatTemplateConfig config = this.config.getGroupTemplates().get(key);
            if (config == null) {
                return this.defaultTemplate;
            }
            return new TemplateCache(config,
                    this.textTemplateFactory.createFromAmpersandStringIgnoringExceptions(config.getPrefix()).orElseGet(NucleusTextTemplateImpl::empty),
                    this.textTemplateFactory.createFromAmpersandStringIgnoringExceptions(config.getSuffix()).orElseGet(NucleusTextTemplateImpl::empty)
            );
        });
    }

    @Override
    public void onReload(final INucleusServiceCollection serviceCollection) {
        this.config = serviceCollection.configProvider().getModuleConfig(ChatConfig.class);
        this.templateCacheMap.clear();
        this.defaultTemplate = new TemplateCache(
                this.config.getDefaultTemplate(),
                serviceCollection.textTemplateFactory().createFromAmpersandStringIgnoringExceptions(this.config.getDefaultTemplate().getPrefix())
                    .orElseGet(NucleusTextTemplateImpl::empty),
                serviceCollection.textTemplateFactory().createFromAmpersandStringIgnoringExceptions(this.config.getDefaultTemplate().getSuffix())
                    .orElseGet(NucleusTextTemplateImpl::empty)
                );
    }

    public TemplateCache getDefaultTemplate() {
        return this.defaultTemplate;
    }

    public static final class TemplateCache {
        private final ChatTemplateConfig config;
        private final NucleusTextTemplate prefix;
        private final NucleusTextTemplate suffix;

        public TemplateCache(final ChatTemplateConfig config, final NucleusTextTemplate prefix, final NucleusTextTemplate suffix) {
            this.config = config;
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public ChatTemplateConfig getConfig() {
            return this.config;
        }

        public NucleusTextTemplate getPrefix() {
            return this.prefix;
        }

        public NucleusTextTemplate getSuffix() {
            return this.suffix;
        }
    }
}
