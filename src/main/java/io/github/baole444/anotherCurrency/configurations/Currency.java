package io.github.baole444.anotherCurrency.configurations;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Currency configuring options.
 * @param canonicalName unique code name of the currency
 * @param displayName user-friendly name for the currency
 * @param prefix text before the balance number
 * @param suffix text after the balance number
 * @param grouping currency balance display truncation options
 */
public record Currency(
        String canonicalName, String displayName,
        String prefix, String suffix,
        Grouping grouping
) {
    public static final String NameKey = "name";
    public static final String PrefixKey = "prefix";
    public static final String SuffixKey = "suffix";
    public static final String GroupingValueKey = "grouping.value";
    public static final String GroupingSymbolKey = "grouping.symbols";

    /**
     * Currency grouping format options.
     * @param value amount of currency unit each group has
     * @param symbols symbol use by each tier of grouping
     */
    public record Grouping(int value, List<String> symbols) {
        /**
         * Compact constructor ensure new grouping's symbol list is not null.
         * @param value amount of currency unit each group has
         * @param symbols symbol use by each tier of grouping
         */
        public Grouping {
            if (symbols == null) symbols = new ArrayList<>();
        }

        /**
         * Create a new {@link Currency} configuration from other vault config's components.
         * @param other the other Currency to copy from
         */
        public Grouping(Grouping other) {
            this(other.value, other.symbols);
        }

        /**
         * Check if grouping is enabled for the currency.
         * @return true if grouping is defined
         */
        public boolean enabled() {
            return value > 0 && !symbols.isEmpty();
        }
    }

    /**
     * Compact constructor ensure new Currency's grouping is not null.
     * @param canonicalName unique code name of the currency
     * @param displayName user-friendly name for the currency
     * @param prefix text before the balance number
     * @param suffix text after the balance number
     * @param grouping currency balance display truncation options
     */
    public Currency {
        if (grouping == null) {
            grouping = new Grouping(0, new ArrayList<>());
        }
    }

    /**
     * Check if the currency's grouping is enabled.
     * @return true if {@link Grouping#enabled()} return true
     */
    public boolean hasGrouping() {
        return grouping.enabled();
    }

    /**
     * Format the balance using this Currency's format.
     * @param value the balance amount
     * @return a component of the formatted text
     */
    public Component format(double value) {
        String formatted = hasGrouping() ? formatWithGroup(value) : String.format("%.2f", value);

        Component prefixComponent = deserializeText(prefix);
        Component valueComponent = Component.text(formatted);
        Component suffixComponent = deserializeText(suffix);

        return Component.text()
                .append(prefixComponent)
                .append(valueComponent)
                .append(suffixComponent)
                .build();
    }

    /**
     * Format the balance using this Currency's format.
     * @param value the balance amount
     * @return the legacy formatting string
     */
    public String formatLegacy(double value) {
        return LegacyComponentSerializer.legacySection().serialize(format(value));
    }

    private Component deserializeText(String text) {
        if (text == null || text.isEmpty()) return Component.empty();

        if (text.contains("<") && text.contains(">")) return MiniMessage.miniMessage().deserialize(text);

        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    private String formatWithGroup(double value) {
        double abs = Math.abs(value);
        String sign = value < 0 ? "-" : "";

        int group = 0;
        double divisor = grouping.value;

        while (group < grouping.symbols.size() - 1 && abs >= divisor * grouping().value) {
            divisor *= grouping.value;
            group++;
        }

        if (abs >= divisor && group < grouping.symbols.size()) {
            double grouped = abs / divisor;
            String symbol = grouping.symbols.get(group);
            if (grouped % 1 == 0) return sign + String.format("%.0f", grouped) + symbol;
            else return sign + String.format("%.2f", grouped) + symbol;
        }

        if (value % 1 == 0) return sign + String.format("%.0f", abs);
        return String.format("%.2f", value);
    }
}
