package io.github.baole444.anotherCurrency.configurations;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

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

    public record Grouping(int value, List<String> symbols) {
        public Grouping {
            if (symbols == null) symbols = new ArrayList<>();
        }

        public Grouping(Grouping other) {
            this(other.value, other.symbols);
        }

        public boolean enabled() {
            return value > 0 && !symbols.isEmpty();
        }
    }

    public Currency {
        if (grouping == null) {
            grouping = new Grouping(0, new ArrayList<>());
        }
    }

    public boolean hasGrouping() {
        return grouping.enabled();
    }

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
