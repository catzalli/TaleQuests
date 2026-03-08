package org.aselstudios.talequests.api;

/**
 * Result of a {@link TaleQuestsProvider#reportProgress} call.
 */
public enum ProgressResult {

    /** Progress was applied to at least one quest requirement. */
    OK,

    /** The player is not online or their data is not loaded. */
    PLAYER_OFFLINE,

    /** No API-registered quests matched the given requirement type and target. */
    NO_MATCHING_QUESTS,

    /** The API has not been initialized yet. */
    API_NOT_READY,

    /** Invalid input parameters (null values, non-positive amount, etc.). */
    INVALID_INPUT
}
