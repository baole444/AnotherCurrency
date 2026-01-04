package io.github.baole444.anotherCurrency.configurations;

/**
 * Players configuring options.
 * @param playtime playtime tracking options
 */
public record Players(Playtime playtime) {
    public static final String PlayersKey = "players";
    public static final String PlaytimeKey = "playtime";
    public static final String TrackPlayTimeKey = "track-playtime";
    public static final String DetectAFKKey = "detect-afk";
    public static final String AFKThresholdKey = "afk-threshold";

    /**
     * Players config full path.
     */
    public static class PlaytimePath {
        private PlaytimePath() {}
        private static final String path = PlayersKey + "." + PlaytimeKey + ".";

        /**
         * Path to track playtime key.
         */
        public static final String TrackPlayTime = path + TrackPlayTimeKey;

        /**
         * Path to detect AFK key.
         */
        public static final String DetectAFK = path + DetectAFKKey;

        /**
         * Path to AFK threshold key.
         */
        public static final String AFKThreshold = path + AFKThresholdKey;
    }

    /**
     * Playtime configuring options.
     * @param trackPlaytime enable tracking player time or not
     * @param detectAFK enable AFK detection and pause playtime counting or not
     * @param afkThreshold time before pausing time counting since AFK, in seconds
     */
    public record Playtime(boolean trackPlaytime, boolean detectAFK, long afkThreshold) {
        /**
         * Create a new {@link Playtime} configuration from other playtime config's components.
         * @param other the other Playtime to copy from
         */
        public Playtime(Playtime other) {
            this(other.trackPlaytime, other.detectAFK, other.afkThreshold);
        }

        /**
         * Get the default Playtime configuration.
         * @return a new {@link Playtime} config option
         */
        public static Playtime getDefault() {
            return new Playtime(false, true, 300L);
        }
    }

    /**
     * Compact constructor ensure that playtime is not null.
     * @param playtime playtime tracking options
     */
    public Players {
        if (playtime == null) playtime = Playtime.getDefault();
    }

    /**
     * Create a new {@link Players} configuration from other players config's components.
     * @param other the other Players to copy from
     */
    public Players(Players other) {
        this(other.playtime);
    }

    /**
     * Get the default Players configuration.
     * @return a new {@link Players} config option
     */
    public static Players getDefault() {
        return new Players(Playtime.getDefault());
    }
}
