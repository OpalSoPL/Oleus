/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.module;

import java.util.Collection;

public interface IModuleProvider {

    Collection<ModuleContainer> getModules();

}
