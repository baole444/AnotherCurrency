package io.github.baole444.anotherCurrency.configurations;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;

public class CurrencyManager {
    public static final String currenciesYML = "currencies.yml";

    private final JavaPlugin plugin;
    private final Map<String, Currency> currencies;
    private File currenciesFile;
    private FileConfiguration config;

    public CurrencyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        currencies = new HashMap<>();
        loadCurrencies();
    }

    public Currency currency(String codeName) {
        return currencies.get(codeName);
    }

    public Map<String, Currency> currencies() {
        return Collections.unmodifiableMap(currencies);
    }

    public Set<String> currencyNames() {
        return Collections.unmodifiableSet(currencies.keySet());
    }

    public int currencyCount() {
        return currencies.size();
    }

    public boolean hasCurrency(String codeName) {
        return currencies.containsKey(codeName);
    }

    public void loadCurrencies() {
        currenciesFile = new File(plugin.getDataFolder(), currenciesYML);
        if (!currenciesFile.exists()) plugin.saveResource(currenciesYML, false);

        config = YamlConfiguration.loadConfiguration(currenciesFile);

        InputStream stream = plugin.getResource(currenciesYML);
        if (stream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            config.setDefaults(defaultConfig);
        }

        currencies.clear();
        Set<String> keys = config.getKeys(false);
        for (String codeName : keys) {
            try {
                Currency currency = loadCurrency(codeName);
                if (currency != null) currencies.put(codeName, currency);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, String.format("Failed to load currency: %s", codeName), e);
            }
        }

        plugin.getLogger().info(String.format("Loaded %d currencies.", currencies.size()));
    }

    public boolean addCurrency(String codeName, String displayName, String prefix, String suffix, int groupingValue, String groupingSymbols) {
        if (hasCurrency(codeName)) return false;

        String canonicalPath = codeName + ".";
        config.set(canonicalPath + Currency.NameKey, displayName);
        config.set(canonicalPath + Currency.PrefixKey, prefix);
        config.set(canonicalPath + Currency.SuffixKey, suffix);
        config.set(canonicalPath + Currency.GroupingValueKey, groupingValue);
        config.set(canonicalPath + Currency.GroupingSymbolKey, groupingSymbols);
        saveConfig();
        loadCurrencies();
        return true;
    }

    public boolean removeCurrency(String codeName) {
        if (!hasCurrency(codeName)) return false;
        config.set(codeName, null);
        saveConfig();
        loadCurrencies();
        return true;
    }

    private Currency loadCurrency(String codeName) {
        ConfigurationSection section = config.getConfigurationSection(codeName);
        if (section == null) return null;

        String displayName = section.getString(Currency.NameKey, "");
        String prefix = section.getString(Currency.PrefixKey, "");
        String suffix = section.getString(Currency.SuffixKey, "");

        int groupingValue = section.getInt(Currency.GroupingValueKey, 0);
        String symbolString = section.getString(Currency.GroupingSymbolKey, "");
        List<String> groupingSymbols = new ArrayList<>();
        if (!symbolString.isEmpty()) {
            String[] symbols = symbolString.split(",");
            groupingSymbols.addAll(Arrays.stream(symbols).toList());
        }

        Currency.Grouping grouping = new Currency.Grouping(groupingValue, groupingSymbols);
        return new Currency(codeName, displayName, prefix, suffix, grouping);
    }

    private void saveConfig() {
        try {
            config.save(currenciesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Could not save %s", currenciesYML), e);
        }
    }
}
