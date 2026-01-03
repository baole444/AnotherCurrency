package io.github.baole444.anotherCurrency;

import io.github.baole444.anotherCurrency.configurations.ConfigManager;
import io.github.baole444.anotherCurrency.configurations.CurrencyManager;
import io.github.baole444.anotherCurrency.integrations.VaultHook;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry of AnotherCurrency (ANC) plugin.
 */
public final class AnotherCurrency extends JavaPlugin {
    /**
     * Version string of AnotherCurrency.
     */
    public static final String Version = "0.1";
    private ConfigManager configManager;
    private CurrencyManager currencyManager;
    private VaultHook vaultHook;

    /**
     * Create the entry instance of AnotherCurrency plugin for the server.
     */
    public AnotherCurrency() {}

    @Override
    public void onEnable() {
        getLogger().info("Checking configurations...");
        configManager = new ConfigManager(this);
        currencyManager = new CurrencyManager(this);
        vaultHook = new VaultHook(this);
        if (vaultHook.setupEconomy()) getLogger().info("Hooking into Vault Economy successfully.");
        getLogger().info("AnotherCurrency enabled.");
    }

    @Override
    public void onDisable() {
        if (vaultHook != null) vaultHook.unregisterEconomy();
        getLogger().info("AnotherCurrency disabled.");
    }

    /**
     * Get the global config manager of ANC.
     * @return the config manager
     */
    public ConfigManager configManager() {
        return configManager;
    }

    /**
     * Get the currency config manager of ANC.
     * @return the currency manager
     */
    public CurrencyManager currencyManager() {
        return currencyManager;
    }

    /**
     * Get the vault integration of ANC.
     * @return the vault hook
     */
    public VaultHook vaultHook() {
        return vaultHook;
    }

    /**
     * Reload all configurations of ANC from disk.
     */
    public void reloadConfigs() {
        configManager.loadConfig();
        currencyManager.loadCurrencies();
        getLogger().info("Configuration reloaded.");
    }
}
