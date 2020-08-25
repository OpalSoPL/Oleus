/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.command.annotation;

import io.github.nucleuspowered.nucleus.scaffold.command.ICommandContext;
import io.github.nucleuspowered.nucleus.scaffold.command.ICommandExecutor;
import io.github.nucleuspowered.nucleus.scaffold.command.control.CommandControl;
import io.github.nucleuspowered.nucleus.scaffold.command.modifier.ICommandModifier;
import io.github.nucleuspowered.nucleus.services.INucleusServiceCollection;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public @interface CommandModifier {

    String value();

    String exemptPermission() default "";

    /**
     * If set, uses the options from the targetted class.
     *
     * @return The class, or {@link ICommandExecutor} if otherwise.
     */
    Class<? extends ICommandExecutor> useFrom() default ICommandExecutor.class;

    /**
     * Type of entity that the modifier acts upon.
     *
     * @return The entity type.
     */
    Class<?> target() default Object.class;

    /**
     * If false, don't generate configuration
     *
     * @return normally true
     */
    boolean generateConfig() default true;

    /**
     * If true, runs the {@link ICommandModifier#preExecute(ICommandContext, CommandControl, INucleusServiceCollection, CommandModifier)}
     * before the command executes.
     *
     * @return true by default
     */
    boolean onExecute() default true;

    /**
     * If true, runs the {@link ICommandModifier#onCompletion(ICommandContext, CommandControl, INucleusServiceCollection, CommandModifier)}
     * after a success result.
     *
     * @return true by default
     */
    boolean onCompletion() default true;

}
