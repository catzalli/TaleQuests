package org.aselstudios.talequests.api.player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Read-only access to quest pool information.
 *
 * <p>Quest pools are daily/weekly rotating quest sets defined in YAML config.
 * This service provides read-only access to pool definitions and player pool state.</p>
 */
public interface PoolService {

    /**
     * Returns a list of all configured pool IDs.
     */
    List<String> getPoolIds();

    /**
     * Returns the display name of a pool, or {@code null} if the pool doesn't exist.
     */
    @Nullable
    String getPoolDisplayName(String poolId);

    /**
     * Returns the reset interval for a pool ({@code "DAILY"} or {@code "WEEKLY"}),
     * or {@code null} if the pool doesn't exist.
     */
    @Nullable
    String getResetInterval(String poolId);

    /**
     * Returns the time remaining (in milliseconds) until the next pool reset,
     * or {@code -1} if the pool doesn't exist.
     */
    long getTimeRemainingMs(String poolId);

    /**
     * Returns a human-readable string of the time remaining until the next pool reset,
     * or {@code null} if the pool doesn't exist.
     */
    @Nullable
    String getFormattedTimeRemaining(String poolId);

    /**
     * Returns a read-only snapshot of a player's pool quest data,
     * or {@code null} if the player data is not loaded or the pool doesn't exist.
     *
     * @param playerId the player's UUID
     * @param poolId   the pool ID
     */
    @Nullable
    PoolQuestInfo getPlayerPoolInfo(UUID playerId, String poolId);

    /**
     * Returns {@code true} if a pool with the given ID exists in config.
     */
    boolean poolExists(String poolId);
}
