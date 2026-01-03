package io.github.baole444.anotherCurrency.integrations;

import io.github.baole444.anotherCurrency.AnotherCurrency;
import io.github.baole444.anotherCurrency.configurations.Currency;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

/**
 * Placeholder for Vault Economy Provider.
 */
public class VaultEconomyProvider implements Economy {
    private final AnotherCurrency plugin;
    private final String primaryCurrency;

    /**
     * Initialize Vault Economy Provider.
     * @param plugin the ANC plugin's instance
     * @param primaryCurrency the code name of the currency to register with vault
     */
    public VaultEconomyProvider(AnotherCurrency plugin, String primaryCurrency) {
        this.plugin = plugin;
        this.primaryCurrency = primaryCurrency;
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return "AnotherCurrency";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        Currency currency = plugin.currencyManager().currency(primaryCurrency);
        return currency != null ? currency.formatLegacy(v) : String.format("%.2f", v);
    }

    @Override
    public String currencyNamePlural() {
        Currency c = plugin.currencyManager().currency(primaryCurrency);
        return c != null ? c.displayName() : primaryCurrency;
    }

    @Override
    public String currencyNameSingular() {
        return currencyNamePlural();
    }

    @Deprecated
    @Override
    public boolean hasAccount(String playerName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String worldName) {
        return false;
    }

    @Override
    @Deprecated
    public double getBalance(String playerName) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return 0;
    }

    @Override
    @Deprecated
    public double getBalance(String playerName, String worldName) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String worldName) {
        return 0;
    }

    @Override
    @Deprecated
    public boolean has(String playerName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return false;
    }

    @Override
    @Deprecated
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String worldName, double v) {
        return false;
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Deprecated
    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
