package io.github.baole444.anotherCurrency;

import io.github.baole444.anotherCurrency.configurations.ConfigManager;
import io.github.baole444.anotherCurrency.configurations.CurrencyManager;
import io.github.baole444.anotherCurrency.data.PlayerDataManager;
import io.github.baole444.anotherCurrency.data.PlaytimeTracker;
import io.github.baole444.anotherCurrency.integrations.VaultHook;
import io.github.baole444.anotherCurrency.listeners.PlayerDataListener;
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
    private PlayerDataManager playerDataManager;
    private PlaytimeTracker playtimeTracker;
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
        playerDataManager = new PlayerDataManager(this);
        playtimeTracker = new PlaytimeTracker(this);
        getServer().getPluginManager().registerEvents(new PlayerDataListener(this), this);

        if (configManager.players().playtime().trackPlaytime()) {
            playtimeTracker.start();
            getLogger().info("Playtime tracking started.");
        }

        vaultHook = new VaultHook(this);
        if (vaultHook.setupEconomy()) getLogger().info("Hooking into Vault Economy successfully.");
        getLogger().info("AnotherCurrency enabled.");
    }

    @Override
    public void onDisable() {
        playtimeTracker.stop();
        playerDataManager.clearCache();
        vaultHook.unregisterEconomy();
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
     * Get the player data manager of ANC.
     * @return the player data manager
     */
    public PlayerDataManager playerDataManager() {
        return playerDataManager;
    }

    /**
     * Get the playtime tracker of ANC.
     * @return the playtime tracker
     */
    public PlaytimeTracker playtimeTracker() {
        return playtimeTracker;
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
        boolean trackingPlaytime = configManager.players().playtime().trackPlaytime();
        configManager.reload();
        currencyManager.loadCurrencies();
        playerDataManager.clearCache();

        boolean stillTrackPlaytime = configManager.players().playtime().trackPlaytime();
        if (stillTrackPlaytime && !trackingPlaytime) {
            playtimeTracker.start();
            getLogger().info("Playtime tracker started.");
        } else if (!stillTrackPlaytime && trackingPlaytime) {
            playtimeTracker.stop();
            getLogger().info("Playtime tracking stopped.");
        }

        getLogger().info("Configuration reloaded.");
    }
}
