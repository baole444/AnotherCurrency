package io.github.baole444.anotherCurrency.integrations;

import io.github.baole444.anotherCurrency.AnotherCurrency;
import io.github.baole444.anotherCurrency.configurations.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.Set;

/**
 * Vault integration of ANC.
 */
public class VaultHook {
    private final AnotherCurrency plugin;
    private VaultEconomyProvider economyProvider;

    /**
     * Initialize the Vault integration.
     * @param plugin the ANC plugin's instance
     */
    public VaultHook(AnotherCurrency plugin) {
        this.plugin = plugin;
    }

    /**
     * Hooking into Vault Economy,
     * using a primary currency defined in {@code config.yml} or first defined in {@code currencies.yml}
     * @return true if registered primary currency successfully
     */
    public boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) return false;

        Vault vaultConfig = plugin.configManager().vault();
        if (!vaultConfig.hookEconomy()) {
            plugin.getLogger().info("Hooking into Vault Economy is disabled in config.");
            return false;
        }

        String primaryCurrency = getPrimaryCurrency(vaultConfig);
        if (primaryCurrency == null) {
            plugin.getLogger().warning("No currencies defined - Hooking into Vault Economy canceled.");
            return false;
        }

        economyProvider = new VaultEconomyProvider(plugin, primaryCurrency);
        ServicesManager servicesManager = plugin.getServer().getServicesManager();

        plugin.getLogger().info(String.format("Registered '%s' as the primary currency with Vault Economy.", primaryCurrency));
        servicesManager.register(Economy.class, economyProvider, plugin, ServicePriority.Normal);
        return true;
    }

    /**
     * Unregister the primary currency with Vault Economy Provider.
     */
    public void unregisterEconomy() {
        if (economyProvider == null) return;
        plugin.getServer().getServicesManager().unregister(Economy.class, economyProvider);
        plugin.getLogger().info("Vault Economy provider unregistered.");
    }

    private String getPrimaryCurrency(Vault config) {
        Set<String> currencies = plugin.currencyManager().currencyNames();
        if (currencies.isEmpty()) return null;
        if (!config.hasPrimaryCurrency()) return currencies.iterator().next();

        String configCurrency = config.primaryCurrency();
        if (plugin.currencyManager().hasCurrency(configCurrency)) return configCurrency;
        return currencies.iterator().next();
    }
}
