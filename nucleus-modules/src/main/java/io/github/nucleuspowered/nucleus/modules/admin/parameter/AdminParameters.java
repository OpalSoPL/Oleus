/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.admin.parameter;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.Entity;

import java.util.function.Predicate;

public final class AdminParameters {

    public final static Parameter.Value<Predicate<Entity>> ENTITY_PARAMETER = Parameter
            .builder(new TypeToken<Predicate<Entity>>() {})
            .addParser(new EntityTypeValueParameter())
            .key("type")
            .consumeAllRemaining()
            .build();

    private AdminParameters() { }

}
