package org.aselstudios.talequests.api.player;

import java.util.UUID;

/**
 * Read-only access to player quest data.
 *
 * <p>Player data is only available while the player is online (or recently
 * disconnected and still cached). Use {@link #isLoaded(UUID)} to check
 * availability before calling {@link #getPlayer(UUID)}.</p>
 */
public interface PlayerDataService {

    /**
     * Returns a read-only snapshot of the player's quest state,
     * or {@code null} if the player's data is not loaded.
     *
     * @param playerId the player's UUID
     */
    QuestPlayer getPlayer(UUID playerId);

    /**
     * Returns {@code true} if the player's quest data is currently loaded in cache.
     *
     * @param playerId the player's UUID
     */
    boolean isLoaded(UUID playerId);
}
