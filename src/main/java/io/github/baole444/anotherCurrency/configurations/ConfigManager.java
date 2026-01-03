package io.github.baole444.anotherCurrency.configurations;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Manager for {@code config.yml}
 */
public class ConfigManager {
    /**
     * Default config file name.
     */
    public static final String ConfigYML = "config.yml";
    private ConfigMigrator migrator;
    private final JavaPlugin plugin;
    private CurrencyLimit currencyLimit;
    private Vault vault;

    /**
     * Initialize config manager instance.
     * @param plugin the ANC plugin's instance
     */
    public ConfigManager(JavaPlugin plugin) {
        migrator = new ConfigMigrator(plugin);
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * Load the config from file or create and save default config.
     */
    public void loadConfig() {
        if (migrator.checkAndMigrate()) plugin.reloadConfig();
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();
        currencyLimit = getCurrencyLimitFromConfig(config);
        vault = getVaultFromConfig(config);
    }

    /**
     * Reload the config from disk.
     */
    public void reload() {
        plugin.reloadConfig();
        loadConfig();
    }

    /**
     * Get the currency limit config.
     * @return the currency limit options
     */
    public CurrencyLimit currencyLimit() {
        return currencyLimit;
    }

    /**
     * Get the vault config.
     * @return the vault hook options
     */
    public Vault vault() {
        return vault;
    }

    /**
     * Update currency limit options to new setting.
     * @param newLimit the options to update with
     * @return true if update successfully
     */
    public boolean updateCurrencyLimit(CurrencyLimit newLimit) {
        this.currencyLimit = newLimit;
        FileConfiguration config = plugin.getConfig();
        return saveCurrencyLimitToConfig(config, newLimit);
    }

    /**
     * Update vault integration option to new setting.
     * @param newVault the options to update with
     * @return true if update successfully
     */
    public boolean updateVault(Vault newVault) {
        this.vault = newVault;
        FileConfiguration config = plugin.getConfig();
        return saveVaultToConfig(config, newVault);
    }

    private CurrencyLimit getCurrencyLimitFromConfig(FileConfiguration config) {
        if (plugin == null || config == null) return CurrencyLimit.getDefault();
        double minTrade = Math.max(0.001, config.getDouble(CurrencyLimit.MinTradePath, 0.1));
        boolean enableMin = config.getBoolean(CurrencyLimit.MinPath.Value, true);
        double minVal = config.getDouble(CurrencyLimit.MinPath.Value, 0.0);
        boolean enableMax = config.getBoolean(CurrencyLimit.MaxPath.Enable, true);
        double maxVal = config.getDouble(CurrencyLimit.MaxPath.Value, 1000000000);

        return new CurrencyLimit(minTrade, enableMin, minVal, enableMax, maxVal);
    }

    private boolean saveCurrencyLimitToConfig(FileConfiguration config, CurrencyLimit newLimit) {
        if (plugin == null || config == null) return false;
        config.set(CurrencyLimit.MinTradePath, newLimit.minTrade());
        config.set(CurrencyLimit.MinPath.Enable, newLimit.enableMin());
        config.set(CurrencyLimit.MinPath.Value, newLimit.minValue());
        config.set(CurrencyLimit.MaxPath.Enable, newLimit.enableMax());
        config.set(CurrencyLimit.MaxPath.Value, newLimit.maxValue());

        plugin.saveConfig();
        return true;
    }

    private Vault getVaultFromConfig(FileConfiguration config) {
        if (plugin == null || config == null) return Vault.getDefault();
        boolean hookEconomy = config.getBoolean(Vault.Path.HookEconomy, true);
        String primaryCurrency = config.getString(Vault.Path.PrimaryCurrency, "");
        boolean hookPermission = config.getBoolean(Vault.Path.HookPermission, true);

        return new Vault(hookEconomy, primaryCurrency, hookPermission);
    }

    private boolean saveVaultToConfig(FileConfiguration config, Vault newVault) {
        if (plugin == null | config == null) return false;
        config.set(Vault.Path.HookPermission, newVault.hookEconomy());
        config.set(Vault.Path.HookPermission, newVault.hasPrimaryCurrency());
        config.set(Vault.Path.HookPermission, newVault.hookPermission());

        plugin.saveConfig();
        return true;
    }
}
