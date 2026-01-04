package io.github.baole444.anotherCurrency.configurations;

/**
 * Currency limit configuring options.
 * @param minTrade minimum value allowed per transaction
 * @param min minimum limit option
 * @param max maximum limit option
 */
public record CurrencyLimit(double minTrade, Min min, Max max) {
    public static final String CurrencyLimitKey = "currency-limit";
    public static final String MinTradeKey = "min-trade";
    public static final String MinTradePath = CurrencyLimitKey + "." + MinTradeKey;
    public static final String MinKey = "min";
    public static final String MaxKey = "max";
    public static final String EnableKey = "enable";
    public static final String ValueKey = "value";

    /**
     * Currency limit config full minimum path.
     */
    public static class MinPath {
        private MinPath() {}
        private static final String path = CurrencyLimitKey + "." + MinKey;

        /**
         * Path to minimum enable key.
         */
        public static final String Enable = path + "." + EnableKey;

        /**
         * Path to minimum value key.
         */
        public static final String Value = path + "." + ValueKey;
    }

    /**
     * Currency limit config full maximum path.
     */
    public static class MaxPath {
        private MaxPath() {}
        private static final String path = CurrencyLimitKey + "." + MaxKey;

        /**
         * Path to maximum enable key.
         */
        public static final String Enable = path + "." + EnableKey;

        /**
         * Path to maximum value key.
         */
        public static final String Value = path + "." + ValueKey;
    }

    /**
     * Currency limit minimum options.
     * @param enable enforce minimum bound or not
     * @param value minimum allowed value
     */
    public record Min(boolean enable, double value) {
        /**
         * Create a new {@link Min} configuration from other min config's components.
         * @param other the other min to copy from
         */
        public Min(Min other) {
            this(other.enable, other.value);
        }
    }

    /**
     * Currency limit maximum options.
     * @param enable enforce maximum bound or not
     * @param value maximum allowed value
     */
    public record Max(boolean enable, double value) {
        /**
         * Create a new {@link Max} configuration from other max config's components.
         * @param other the other max to copy from
         */
        public Max(Max other) {
            this(other.enable, other.value);
        }
    }

    /**
     * Currency limit configuring options.
     * @param minTrade minimum value allowed per transaction
     * @param enableMin enforce minimum bound or not
     * @param minValue minimum bound value
     * @param enableMax enforce maximum bound or not
     * @param maxValue maximum bound value
     */
    public CurrencyLimit(double minTrade, boolean enableMin, double minValue, boolean enableMax, double maxValue) {
        this(minTrade, new Min(enableMin, minValue), new Max(enableMax, maxValue));
    }

    /**
     * Check if minimum bound is enforced or not.
     * @return true if enabled
     */
    public boolean enableMin() {
        return min.enable;
    }

    /**
     * Get the minimum enforced value.
     * @return minimum bound value
     */
    public double minValue() {
        return min.value;
    }

    /**
     * Check if maximum bound is enforced or not.
     * @return true if enabled
     */
    public boolean enableMax() {
        return max.enable;
    }

    /**
     * Get the maximum forced value.
     * @return maximum bound value
     */
    public double maxValue() {
        return max.value;
    }

    /**
     * Check if global limits are not enforced.
     * @return true if two bounds have the same value or both min and max limit are disabled
     */
    public boolean hasNoLimit() {
        return (!min.enable && !max.enable) || (Math.abs(min.value - max.value) <= 0.0001d);
    }

    /**
     * Get the default currency limit configuration.
     * @return a new {@link CurrencyLimit} config option
     */
    public static CurrencyLimit getDefault() {
        return new CurrencyLimit(0.1,
                new CurrencyLimit.Min(true, 0.0),
                new CurrencyLimit.Max(true, 1000000000)
        );
    }
}
