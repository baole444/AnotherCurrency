package io.github.baole444.anotherCurrency.listeners;

import io.github.baole444.anotherCurrency.AnotherCurrency;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener for player data cache management.
 */
public class PlayerDataListener implements Listener {
    private final AnotherCurrency plugin;

    /**
     * Initialize player data listener.
     * @param plugin the ANC plugin's instance
     */
    public PlayerDataListener(AnotherCurrency plugin) {
        this.plugin = plugin;
    }

    /**
     * Load player data into cache when they join.
     * @param event the player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.playerDataManager().playerData(event.getPlayer());
    }

    /**
     * Unload player from cache when they quit.
     * @param event the player quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.playerDataManager().unloadPlayerData(event.getPlayer());
    }
}
