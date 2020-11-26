/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.util;

import com.google.inject.Singleton;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.manager.CommandMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

@Singleton
public final class CommandNameCache {

    public static final CommandNameCache INSTANCE = new CommandNameCache();

    private final Map<CommandMapping, Set<String>> mappingSetMap = new WeakHashMap<>();

    private CommandNameCache() {}

    public Set<String> getFromCommandAndSource(final String command, final CommandCause source) {
        final Optional<? extends CommandMapping> oc = Sponge.getCommandManager().getCommandMapping(command)
                .filter(x -> x.getRegistrar().canExecute(source, x));
        return oc.map(CommandNameCache.INSTANCE::getLowercase).orElseGet(HashSet::new);
    }

    public Set<String> getLowercase(final CommandMapping mapping) {
        return new HashSet<>(this.mappingSetMap
                .computeIfAbsent(mapping, x -> x.getAllAliases().stream().map(String::toLowerCase).collect(Collectors.toSet())));
    }
}
