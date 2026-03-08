package org.aselstudios.talequests.api;

/**
 * Internal bootstrap used by the TaleQuests plugin to initialize and
 * shut down the public API. This class is <b>excluded</b> from the
 * published API jar — external plugins cannot and should not use it.
 */
public final class ApiBootstrap {

    private ApiBootstrap() {}

    /**
     * Initializes the API with the given provider implementation.
     * Called once during TaleQuests plugin setup.
     *
     * @param provider the provider instance (must not be null)
     */
    public static void initialize(TaleQuestsProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider must not be null");
        }
        TaleQuestsAPI.setProvider(provider);
    }

    /**
     * Clears the API provider. Called during TaleQuests plugin shutdown.
     */
    public static void shutdown() {
        TaleQuestsAPI.setProvider(null);
    }
}
