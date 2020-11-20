/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.interfaces;

import com.google.inject.ImplementedBy;
import io.github.nucleuspowered.nucleus.services.impl.economy.EconomyServiceProvider;

import java.util.UUID;

@ImplementedBy(EconomyServiceProvider.class)
public interface IEconomyServiceProvider {

    boolean serviceExists();

    String getCurrencySymbol(double cost);

    boolean hasBalance(UUID src, double balance);

    boolean withdrawFromPlayer(UUID src, double cost);

    boolean withdrawFromPlayer(UUID src, double cost, boolean message);

    boolean depositInPlayer(UUID src, double cost);

    boolean depositInPlayer(UUID src, double cost, boolean message);
}
