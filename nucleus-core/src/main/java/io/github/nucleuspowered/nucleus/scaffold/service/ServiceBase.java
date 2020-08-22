/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.scaffold.service;

import io.github.nucleuspowered.nucleus.Constants;
import io.github.nucleuspowered.nucleus.annotationprocessor.Store;
import io.github.nucleuspowered.nucleus.scaffold.EntryPoint;
/**
 * Marker interface to indicate to the AP that this is a service
 */
@EntryPoint
@Store(Constants.SERVICE)
public interface ServiceBase {

}
