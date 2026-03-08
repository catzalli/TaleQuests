package org.aselstudios.talequests.api;

/**
 * Static entry point for the TaleQuests API.
 *
 * <p>External plugins should call {@link #get()} to obtain the
 * {@link TaleQuestsProvider} instance, which exposes all API services
 * (quest registry, category registry, player data, events, etc.).</p>
 *
 * <pre>{@code
 * if (TaleQuestsAPI.isAvailable()) {
 *     TaleQuestsProvider api = TaleQuestsAPI.get();
 *     api.getQuestRegistry().register(...);
 * }
 * }</pre>
 */
public final class TaleQuestsAPI {

    private static volatile TaleQuestsProvider provider;

    private TaleQuestsAPI() {}

    /**
     * Returns the API provider.
     *
     * @throws IllegalStateException if the API is not yet initialized
     *         (TaleQuests plugin not loaded or still starting up)
     */
    public static TaleQuestsProvider get() {
        TaleQuestsProvider p = provider;
        if (p == null) {
            throw new IllegalStateException(
                    "TaleQuests API is not available. "
                    + "Ensure TaleQuests is installed and has finished loading.");
        }
        return p;
    }

    /**
     * Returns {@code true} if the API is initialized and ready to use.
     */
    public static boolean isAvailable() {
        return provider != null;
    }

    static void setProvider(TaleQuestsProvider p) {
        if (p != null && provider != null) {
            throw new IllegalStateException(
                    "TaleQuests API is already initialized. Cannot replace provider.");
        }
        provider = p;
    }
}
