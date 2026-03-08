package org.aselstudios.talequests.api.player;

/**
 * Represents the status of a quest for a specific player.
 */
public enum QuestStatus {

    /** The player has not started this quest and has no progress. */
    NOT_STARTED,

    /** The player has active progress on this quest. */
    IN_PROGRESS,

    /** The player has completed this quest. */
    COMPLETED,

    /**
     * The quest exists but the player cannot start it yet
     * (e.g., prerequisites not met).
     */
    LOCKED
}
