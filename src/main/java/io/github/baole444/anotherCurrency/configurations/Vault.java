package io.github.baole444.anotherCurrency.configurations;

/**
 * Vault configuring options.
 * @param hookEconomy enable/disable economy hook with Vault Economy Provider
 * @param primaryCurrency name of the primary currency to hook into Vault Economy
 * @param hookPermission enabled/disabled permission hook with Vault Permission Provider
 */
public record Vault(boolean hookEconomy, String primaryCurrency, boolean hookPermission) {
    public static final String VaultKey = "vault";
    public static final String HookEconomyKey = "hook-economy";
    public static final String PrimaryCurrencyKey = "primary-currency";
    public static final String HookPermissionKey = "hook-permission";

    /**
     * Vault config full path.
     */
    public static class Path {
        private Path() {}
        private static final String path = VaultKey + ".";

        /**
         * Path to hook economy key.
         */
        public static final String HookEconomy = path + HookEconomyKey;

        /**
         * Path to primary currency key.
         */
        public static final String PrimaryCurrency = path + PrimaryCurrencyKey;

        /**
         * Path to hook permission key.
         */
        public static final String HookPermission = path + HookPermissionKey;
    }

    /**
     * Create a new {@link Vault} configuration from other vault config's components.
     * @param other the other config to copy from
     */
    public Vault(Vault other) {
        this(other.hookEconomy, other.primaryCurrency, other.hookPermission);
    }

    /**
     * Check if the primary currency is defined.
     * @return true if primary currency is not null and not empty
     */
    public boolean hasPrimaryCurrency() {
        return primaryCurrency != null && !primaryCurrency.isBlank();
    }

    /**
     * Get the default vault configuration.
     * @return a new {@link Vault} config option
     */
    public static Vault getDefault() {
        return new Vault(true, "", true);
    }
}
