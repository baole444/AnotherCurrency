package io.github.baole444.anotherCurrency.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Player data for currency balances and playtime.
 * @param uuid the unique identifier of the player
 * @param playerName last known name of the player
 * @param balances map of currency code name and balance amount
 * @param playtime tracked playtime in seconds
 */
public record PlayerData(UUID uuid, String playerName, Map<String, Double> balances, long playtime) {
    public static final int DataVersion = 1;
    public static final String DataVersionKey = "data-version";
    public static final String PlayerNameKey = "player-name";
    public static final String BalancesKey = "balances";
    public static final String PlaytimeKey = "playtime";

    /**
     * Compact constructor ensure new player data's balance map is not null.
     * @param uuid the unique identifier of the player
     * @param playerName last known name of the player
     * @param balances map of currency code name and balance amount
     */
    public PlayerData {
        if (balances == null) balances = new HashMap<>();
    }

    /**
     * Create a new {@link PlayerData} with empty balance map.
     * @param uuid the unique identifier of the player
     * @param playerName last known name of the player
     */
    public PlayerData(UUID uuid, String playerName) {
        this(uuid, playerName, new HashMap<>(), 0L);
    }

    /**
     * Get the balance for a specific currency.
     * @param currencyCode the canonical name of the currency
     * @return the amount of currency unit
     */
    public double balance(String currencyCode) {
        return balances.getOrDefault(currencyCode, 0.0);
    }

    /**
     * Set the balance for a specific currency.
     * @param currencyCode the canonical name of the currency
     * @param amount the new balance amount
     */
    public void balance(String currencyCode, double amount) {
        balances.put(currencyCode, amount);
    }

    /**
     * Remove balance data for a specific currency.
     * @param currencyCode the canonical name of the currency
     * @return true if removed successfully
     */
    public boolean removeBalance(String currencyCode) {
        return balances.remove(currencyCode) != null;
    }

    /**
     * Check if the player has balance data for a currency or not.
     * @param currencyCode the canonical name of the currency
     * @return true if balance exists
     */
    public boolean hasBalance(String currencyCode) {
        return balances.containsKey(currencyCode);
    }

    /**
     * Set the playtime for the play data.
     * @param seconds playtime to set, in second
     * @return a new {@link PlayerData} of current data with updated playtime
     */
    public PlayerData playtime(long seconds) {
        return new PlayerData(uuid, playerName, balances, seconds);
    }

    /**
     * Add playtime to the play data
     * @param additionalSeconds playtime to add, in second
     * @return a new {@link PlayerData} of current data with updated playtime
     */
    public PlayerData addPlaytime(long additionalSeconds) {
        return new PlayerData(uuid, playerName, balances, playtime + additionalSeconds);
    }
}
