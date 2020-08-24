/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.services.impl.economy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.services.interfaces.IEconomyServiceProvider;
import io.github.nucleuspowered.nucleus.services.interfaces.IMessageProviderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Optional;

@Singleton
public class EconomyServiceProvider implements IEconomyServiceProvider {

    private final IMessageProviderService messageProviderService;

    @Inject
    public EconomyServiceProvider(final IMessageProviderService messageProviderService) {
        this.messageProviderService = messageProviderService;
    }

    @Override
    public boolean serviceExists() {
        return Sponge.getServiceProvider().economyService().isPresent();
    }

    @Override public String getCurrencySymbol(final double cost) {
        final Optional<EconomyService> oes = Sponge.getServiceProvider().economyService();
        return oes.map(economyService -> economyService.getDefaultCurrency().format(BigDecimal.valueOf(cost)).toString())
                .orElseGet(() -> String.valueOf(cost));

    }

    @Override public boolean hasBalance(final Player src, final double balance) {
        final Optional<EconomyService> oes = Sponge.getServiceProvider().economyService();
        if (oes.isPresent()) {
            // Check balance.
            final EconomyService es = oes.get();
            final Optional<UniqueAccount> ua = es.getOrCreateAccount(src.getUniqueId());
            return ua.isPresent() && ua.get().getBalance(es.getDefaultCurrency()).doubleValue() >= balance;
        }

        // No economy
        return true;
    }

    @Override public boolean withdrawFromPlayer(final Player src, final double cost) {
        return this.withdrawFromPlayer(src, cost, true);
    }

    @Override public boolean withdrawFromPlayer(final Player src, final double cost, final boolean message) {
        final Optional<EconomyService> oes = Sponge.getServiceProvider().economyService();
        if (oes.isPresent()) {
            // Check balance.
            final EconomyService es = oes.get();
            final Optional<UniqueAccount> a = es.getOrCreateAccount(src.getUniqueId());
            if (!a.isPresent()) {
                this.messageProviderService.sendMessageTo(src, "cost.noaccount");
                return false;
            }

            // TODO: try (final CauseStackManager.StackFrame frame = )
            final TransactionResult tr = a.get().withdraw(es.getDefaultCurrency(), BigDecimal.valueOf(cost));
            if (tr.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                if (message) {
                    this.messageProviderService.sendMessageTo(src, "cost.nofunds", this.getCurrencySymbol(cost));
                }

                return false;
            } else if (tr.getResult() != ResultType.SUCCESS) {
                this.messageProviderService.sendMessageTo(src, "cost.error");
                return false;
            }

            if (message) {
                this.messageProviderService.sendMessageTo(src, "cost.complete", this.getCurrencySymbol(cost));
            }
        }

        return true;
    }

    @Override public boolean depositInPlayer(final User src, final double cost) {
        return this.depositInPlayer(src, cost, true);
    }

    @Override public boolean depositInPlayer(final User src, final double cost, final boolean message) {
        final Optional<EconomyService> oes = Sponge.getServiceProvider().economyService();
        if (oes.isPresent()) {
            // Check balance.
            final EconomyService es = oes.get();
            final Optional<UniqueAccount> a = es.getOrCreateAccount(src.getUniqueId());
            if (!a.isPresent()) {
                src.getPlayer().ifPresent(x ->
                        this.messageProviderService.sendMessageTo(x, "cost.noaccount"));
                return false;
            }

            final TransactionResult tr = a.get().deposit(es.getDefaultCurrency(), BigDecimal.valueOf(cost));
            if (tr.getResult() != ResultType.SUCCESS && src.isOnline()) {
                src.getPlayer().ifPresent(x ->
                        this.messageProviderService.sendMessageTo(x, "cost.error"));
                return false;
            }

            if (message && src.isOnline()) {
                src.getPlayer().ifPresent(x ->
                        this.messageProviderService.sendMessageTo(x, "cost.refund", this.getCurrencySymbol(cost)));
            }
        }

        return true;
    }

}
