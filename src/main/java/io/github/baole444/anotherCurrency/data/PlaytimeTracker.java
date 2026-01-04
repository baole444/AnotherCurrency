package io.github.baole444.anotherCurrency.data;

import io.github.baole444.anotherCurrency.AnotherCurrency;
import io.github.baole444.anotherCurrency.configurations.Players;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Playtime tracking manager.
 */
public class PlaytimeTracker {
    private static final long UpdateInterval = 60L;
    private static final long UpdateIntervalTick = UpdateInterval * 20L;

    private final AnotherCurrency plugin;
    private final ConcurrentHashMap<UUID, Long> lastUpdateTime;
    private BukkitRunnable task;

    /**
     * Initialize playtime tracker instance.
     * @param plugin the ANC plugin's instance
     */
    public PlaytimeTracker(AnotherCurrency plugin) {
        this.plugin = plugin;
        lastUpdateTime = new ConcurrentHashMap<>();
    }

    /**
     * Start the playtime tracking task.
     */
    public void start() {
        if (task != null && !task.isCancelled()) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                updatePlaytime();
            }
        };

        task.runTaskTimer(plugin, 20L, UpdateIntervalTick);
    }

    /**
     * Stop the playtime tracking task
     */
    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }

        lastUpdateTime.clear();
    }

    /**
     * Add player to playtime tracker on join event.
     * @param player the player that joined
     */
    public void onPlayerJoin(Player player) {
        lastUpdateTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * Save player playtime on quit event.
     * @param player the player that quit
     */
    public void onPlayerQuit(Player player) {
        UUID uuid = player.getUniqueId();
        if (shouldExcludeAFK(player)) lastUpdateTime.remove(uuid);

        long elapsed = getElapsedSecond(uuid);
        if (elapsed > 0) plugin.playerDataManager().addPlaytime(player, elapsed);
        lastUpdateTime.remove(uuid);
    }

    private boolean shouldExcludeAFK(Player player) {
        Players.Playtime config = plugin.configManager().players().playtime();
        if (!config.detectAFK()) return false;
        return isAFK(player, config.afkThreshold());
    }

    private boolean isAFK(Player player, long threshold) {
        Duration idleDuration = player.getIdleDuration();
        return idleDuration.getSeconds() >= threshold;
    }

    private void updatePlaytime() {
        Players.Playtime config = plugin.configManager().players().playtime();
        if (!config.trackPlaytime()) return;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (!lastUpdateTime.containsKey(uuid)) {
                lastUpdateTime.put(uuid, System.currentTimeMillis());
                continue;
            }

            if (!shouldExcludeAFK(player)) {
                long elapsed = getElapsedSecond(uuid);
                if (elapsed > 0) plugin.playerDataManager().addPlaytime(player, elapsed);
            }

            lastUpdateTime.put(uuid, System.currentTimeMillis());
        }
    }

    private long getElapsedSecond(UUID uuid) {
        Long lastUpdate = lastUpdateTime.get(uuid);
        if (lastUpdate == null) return 0L;

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastUpdate;
        return elapsed / 1000L;
    }
}
