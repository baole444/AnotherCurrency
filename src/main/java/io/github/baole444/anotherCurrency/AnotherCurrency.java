package io.github.baole444.anotherCurrency;

import io.github.baole444.anotherCurrency.configurations.ConfigManager;
import io.github.baole444.anotherCurrency.configurations.CurrencyManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AnotherCurrency extends JavaPlugin {
    public static final String Version = "0.1";
    private ConfigManager configManager;
    private CurrencyManager currencyManager;

    @Override
    public void onEnable() {
        getLogger().info("Checking configurations...");
        configManager = new ConfigManager(this);
        currencyManager = new CurrencyManager(this);
        getLogger().info("AnotherCurrency enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AnotherCurrency disabled.");
    }

    public ConfigManager configManager() {
        return configManager;
    }

    public CurrencyManager currencyManager() {
        return currencyManager;
    }

    public void reloadConfigs() {
        configManager.loadConfig();
        currencyManager.loadCurrencies();
        getLogger().info("Configuration reloaded");
    }
}
