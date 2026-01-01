package io.github.baole444.anotherCurrency.configurations;

public record CurrencyLimit(double minTrade, Min min, Max max) {
    public static final String CurrencyLimitKey = "currency-limit";
    public static final String MinTradeKey = "min-trade";
    public static final String MinTradePath = CurrencyLimitKey + "." + MinTradeKey;

    public static final String MinKey = "min";
    public static final String MaxKey = "max";

    public static final String EnableKey = "enable";
    public static final String ValueKey = "value";

    public static class MinPath {
        private static final String path = CurrencyLimitKey + "." + MinKey;
        public static final String Enable = path + "." + EnableKey;
        public static final String Value = path + "." + ValueKey;
    }

    public static class MaxPath {
        private static final String path = CurrencyLimitKey + "." + MaxKey;
        public static final String Enable = path + "." + EnableKey;
        public static final String Value = path + "." + ValueKey;
    }

    public record Min(boolean enable, double value) {
        public Min(Min other) {
            this(other.enable, other.value);
        }
    }

    public record Max(boolean enable, double value) {
        public Max(Max other) {
            this(other.enable, other.value);
        }
    }

    public CurrencyLimit(double minTrade, boolean enableMin, double minValue, boolean enableMax, double maxValue) {
        this(minTrade, new Min(enableMin, minValue), new Max(enableMax, maxValue));
    }

    public boolean enableMin() {
        return min.enable;
    }

    public double minValue() {
        return min.value;
    }

    public boolean enableMax() {
        return max.enable;
    }

    public double maxValue() {
        return max.value;
    }

    public boolean hasNoLimit() {
        return (!min.enable && !max.enable) || (Math.abs(min.value - max.value) <= 0.0001d);
    }

    public static CurrencyLimit getDefault() {
        return new CurrencyLimit(0.1,
                new CurrencyLimit.Min(true, 0.0),
                new CurrencyLimit.Max(true, 1000000000)
        );
    }
}
