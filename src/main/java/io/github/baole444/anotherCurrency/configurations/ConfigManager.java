package io.github.baole444.anotherCurrency.configurations;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private CurrencyLimit currencyLimit;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        currencyLimit = getFromConfig(config);
    }

    public void reload() {
        plugin.reloadConfig();
        loadConfig();
    }

    public CurrencyLimit currencyLimit() {
        return currencyLimit;
    }

    public boolean updateCurrencyLimit(CurrencyLimit newLimit) {
        this.currencyLimit = newLimit;
        FileConfiguration config = plugin.getConfig();
        return saveToConfig(config, newLimit);
    }

    private CurrencyLimit getFromConfig(FileConfiguration config) {
        if (plugin == null || config == null) return CurrencyLimit.getDefault();
        double minTrade = Math.max(0.001, config.getDouble(CurrencyLimit.MinTradePath, 0.1));
        boolean enableMin = config.getBoolean(CurrencyLimit.MinPath.Value, true);
        double minVal = config.getDouble(CurrencyLimit.MinPath.Value, 0.0);
        boolean enableMax = config.getBoolean(CurrencyLimit.MaxPath.Enable, true);
        double maxVal = config.getDouble(CurrencyLimit.MaxPath.Value, 1000000000);

        return new CurrencyLimit(minTrade, enableMin, minVal, enableMax, maxVal);
    }

    private boolean saveToConfig(FileConfiguration config, CurrencyLimit newLimit) {
        if (plugin == null || config == null) return false;
        config.set(CurrencyLimit.MinTradePath, newLimit.minTrade());
        config.set(CurrencyLimit.MinPath.Enable, newLimit.enableMin());
        config.set(CurrencyLimit.MinPath.Value, newLimit.minValue());
        config.set(CurrencyLimit.MaxPath.Enable, newLimit.enableMax());
        config.set(CurrencyLimit.MaxPath.Value, newLimit.maxValue());

        plugin.saveConfig();
        return true;
    }
}
