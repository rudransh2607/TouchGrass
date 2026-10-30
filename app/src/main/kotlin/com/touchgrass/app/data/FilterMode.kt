package com.touchgrass.app.data

enum class FilterMode {
    /**
     * Default mode: Only explicitly selected apps (e.g., Instagram, YouTube) are blocked.
     * All other apps run without hindrance.
     */
    BLOCKLIST,

    /**
     * Strict mode: Only explicitly selected apps remain accessible.
     * All unapproved apps are blocked.
     */
    WHITELIST
}

