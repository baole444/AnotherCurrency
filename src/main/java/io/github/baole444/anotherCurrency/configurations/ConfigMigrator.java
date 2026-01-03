package io.github.baole444.anotherCurrency.configurations;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Experimental Config Update and Migrator.
 */
public class ConfigMigrator {
    /**
     * The up-to-date config version and formatting.
     */
    public static final int ConfigVersion = 1;

    /**
     * Config version key.
     */
    public static final String VersionKey = "config-version";

    /**
     * A set of config file version that contain breaking changes, these often are renamed key, data structure changes.
     */
    private static final Set<Integer> breakingChanges = Set.of();

    private final JavaPlugin plugin;

    /**
     * Initialize config migrator instance.
     * @param plugin the ANC plugin's instance
     */
    public ConfigMigrator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Check the version of the config file and update it accordingly.
     * @return true if updated,
     * false if there is no config or config version is higher than the plugin's expected version
     */
    public boolean checkAndMigrate() {
        File configFile = new File(plugin.getDataFolder(), ConfigManager.ConfigYML);

        if (!configFile.exists()) return false;
        FileConfiguration config = plugin.getConfig();
        int fileVersion = config.getInt(VersionKey, 0);

        if (fileVersion >= ConfigVersion) return false;
        plugin.getLogger().info(String.format("Updating config version %d to version %d...", fileVersion, ConfigVersion));
        long startTime = System.currentTimeMillis();
        backupConfig(configFile, fileVersion);
        migrateToLatest(fileVersion);
        long finishTime = System.currentTimeMillis();
        plugin.getLogger().info(String.format("Config finishing updating. Took %dms.", finishTime - startTime));
        return true;
    }

    private void migrateToLatest(int oldVersion) {
        int current = oldVersion;
        while (current < ConfigVersion) {
            int nextBreakingChange = getNextBreakingChange(current);
            if (nextBreakingChange <= current) {
                updateConfig(ConfigVersion);
                current = ConfigVersion;
            } else {
                int target = nextBreakingChange -1;
                if (target > current) {
                    updateConfig(target);
                    current = target;
                }

                updateBreakingChanges(current, nextBreakingChange);
                current = nextBreakingChange;
            }
        }
    }

    private int getNextBreakingChange(int from) {
        for (int v = from + 1; v <= ConfigVersion; v++) {
            if (breakingChanges.contains(v)) return v;
        }

        return -1;
    }

    private void updateConfig(int target) {
        FileConfiguration oldConfig = plugin.getConfig();

        Map<String, Object> currentValues = new HashMap<>();
        for (String key : oldConfig.getKeys(true)) {
            if (!oldConfig.isConfigurationSection(key)) currentValues.put(key, oldConfig.get(key));
        }

        if (!replaceDefault()) {
            plugin.getLogger().warning("Failed to remove old config, update config might failed.");
            return;
        }

        FileConfiguration newConfig = plugin.getConfig();
        for (Map.Entry<String, Object> entry : currentValues.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(VersionKey) && newConfig.contains(key)) newConfig.set(key, entry.getValue());
        }

        newConfig.set(VersionKey, target);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    private void updateBreakingChanges(int from, int to) {
        FileConfiguration oldConfig = plugin.getConfig();

        //Breaking changes remap logic goes here.

        updateConfig(to);
    }

    private boolean replaceDefault() {
        File configFile = new File(plugin.getDataFolder(), ConfigManager.ConfigYML);
        if (configFile.delete()) {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            return true;
        }

        return false;
    }

    private void backupConfig(File configFile, int version) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            File backup = new File(plugin.getDataFolder(), String.format("config.yml.v%d.backup.%s", version, timestamp));

            Files.copy(configFile.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);

            plugin.getLogger().info(String.format("Config backup created: %s", backup.getName()));
        } catch (IOException e) {
            plugin.getLogger().warning(String.format("Failed to backup config: %s", e.getMessage()));
        }
    }
}
