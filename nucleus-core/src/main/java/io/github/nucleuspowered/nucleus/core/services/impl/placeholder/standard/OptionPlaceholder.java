/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.placeholder.standard;

import io.github.nucleuspowered.nucleus.core.services.interfaces.IPermissionService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.service.permission.Subject;

import java.util.Optional;

public class OptionPlaceholder implements PlaceholderParser {

    private static final ResourceKey KEY = ResourceKey.resolve("nucleus:option");
    private final IPermissionService permissionService;

    public OptionPlaceholder(final IPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public Component parse(final PlaceholderContext placeholderContext) {
        final Optional<Subject> subjectOptional = placeholderContext.associatedObject()
                .filter(x -> x instanceof Subject)
                .map(x -> (Subject) x);
        if (subjectOptional.isPresent() && placeholderContext.argumentString().isPresent()) {
            return this.permissionService
                    .getOptionFromSubject(subjectOptional.get(), placeholderContext.argumentString().get())
                    .map(LegacyComponentSerializer.legacyAmpersand()::deserialize)
                    .orElse(Component.empty());
        }
        return Component.empty();
    }

}
