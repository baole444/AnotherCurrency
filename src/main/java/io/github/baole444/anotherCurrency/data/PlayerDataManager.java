package io.github.baole444.anotherCurrency.data;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Manager for player data files.
 */
public class PlayerDataManager {
    /**
     * Name of the directory that store player data.
     */
    public static final String PlayerDataDirName = "players";

    /**
     * Format for the name of each player data file, which is {@code {uuid}.yml}.
     */
    public static final String PlayerFileNameFormat = "%s.yml";
    private final JavaPlugin plugin;
    private final File playersDir;
    private final ConcurrentHashMap<UUID, PlayerData> cache;

    /**
     * Initial8ize player data manager instance.
     * @param plugin the ANC plugin's instance
     */
    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        playersDir = new File(plugin.getDataFolder(), PlayerDataDirName);
        cache = new ConcurrentHashMap<>();

        if (!playersDir.exists() && !playersDir.mkdirs()) {
            plugin.getLogger().warning(String.format("Failed to create %s directory, player data might not able to be saved correctly.", PlayerDataDirName));
        }
    }

    /**
     * Get player data, this will load from disk if dat is not cached.
     * @param player the player to get data from
     * @return the player data, or empty data if not found
     */
    public PlayerData playerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        return cache.computeIfAbsent(uuid, k -> {
           PlayerData loaded = loadPlayerData(player);
           if (loaded == null) return new PlayerData(uuid, player.getName());

           return loaded;
        });
    }

    /**
     * Get the balance of a specific currency from the player.
     * @param player the player to check
     * @param currencyCode the canonical name of the currency
     * @return the balance amount
     */
    public double balance(OfflinePlayer player, String currencyCode) {
        return playerData(player).balance(currencyCode);
    }

    /**
     * Set the balance of a specific currency for the player.
     * @param player the player to set
     * @param currencyCode the canonical name of the currency
     * @param amount the balance amount
     * @return true if save data successfully
     */
    public boolean balance(OfflinePlayer player, String currencyCode, double amount) {
        PlayerData data = playerData(player);
        data.balance(currencyCode, amount);
        return savePlayerData(data);
    }

    /**
     * Check if the player has sufficient balance for a specific currency or not.
     * @param player the player to check
     * @param currencyCode the canonical name of the currency
     * @param amount the amount of balance required
     * @return true if there is enough
     */
    public boolean hasBalance(OfflinePlayer player, String currencyCode, double amount) {
        return balance(player, currencyCode) >= amount;
    }

    /**
     * Save player data to disk.
     * @param data the player data to save
     * @return true if save successfully
     */
    public boolean savePlayerData(PlayerData data) {
        File playerFile = getPlayerFile(data.uuid());
        FileConfiguration config = new YamlConfiguration();
        config.set(PlayerData.DataVersionKey, PlayerData.DataVersion);
        config.set(PlayerData.PlayerNameKey, data.playerName());
        config.set(PlayerData.PlayerNameKey, data.playerName());
        config.createSection(PlayerData.BalancesKey, data.balances());

        try {
            config.save(playerFile);
            cache.put(data.uuid(), data);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Failed to save player data for %s", data.uuid()), e);
            return false;
        }
    }

    /**
     * Load player data from disk.
     * @param player the player to load data for
     * @return the loaded player data or null if the file does not exist
     */
    public PlayerData loadPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        File playerFile = getPlayerFile(uuid);

        if (!playerFile.exists()) return null;

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        String playerName = config.getString(PlayerData.PlayerNameKey, player.getName());

        Map<String, Double> balances = new HashMap<>();
        ConfigurationSection balanceSection = config.getConfigurationSection(PlayerData.BalancesKey);

        if (balanceSection != null) {
            for (String key : balanceSection.getKeys(false)) {
                balances.put(key, balanceSection.getDouble(key, 0.0));
            }
        }

        PlayerData data = new PlayerData(uuid, playerName, balances);
        cache.put(uuid, data);
        return data;
    }

    /**
     * Unload the data from cache.
     * @param player the player to unlock
     */
    public void unloadPlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = cache.remove(uuid);

        if (data != null) savePlayerData(data);
    }

    /**
     * Check if player data is on disk yet.
     * @param player the player to check
     * @return true if data file exists
     */
    public boolean hasPlayerData(OfflinePlayer player) {
        return getPlayerFile(player.getUniqueId()).exists();
    }

    /**
     * Delete player data from disk..
     * @param player the player to delete
     * @return true if delete successfully
     */
    public boolean deletePlayerData(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        cache.remove(uuid);
        File playerFile = getPlayerFile(uuid);
        if (playerFile.exists()) return playerFile.delete();

        return true;
    }

    /**
     * Save all cached player data to disk.
     */
    public void saveAll() {
        for (PlayerData data : cache.values()) savePlayerData(data);
        plugin.getLogger().info(String.format("Saved %d player data.", cache.size()));
    }

    /**
     * Save all cached player data to disk and clear cache.
     */
    public void clearCache() {
        saveAll();
        cache.clear();
    }

    /**
     * Get the number of player data cached in memory.
     * @return size of cache
     */
    public int cacheSize() {
        return cache.size();
    }

    private File getPlayerFile(UUID uuid) {
        return new File(playersDir, String.format(PlayerFileNameFormat, uuid.toString()));
    }
}
