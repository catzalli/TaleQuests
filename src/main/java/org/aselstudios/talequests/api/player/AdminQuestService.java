package org.aselstudios.talequests.api.player;

import java.util.UUID;

/**
 * Administrative operations on player quest data.
 *
 * <p><b>WARNING:</b> These methods bypass the normal quest flow.
 * They do <b>not</b> deliver rewards, remove items, or fire events.
 * Use only for admin tools, debug commands, or data migration.</p>
 *
 * <p>For normal quest progression, use
 * {@link org.aselstudios.talequests.api.TaleQuestsProvider#reportProgress}.</p>
 */
public interface AdminQuestService {

    // Regular Quests

    /**
     * Force-completes a quest for the player. Adds the quest to the
     * completed list and removes any active progress.
     *
     * <p>Does <b>not</b> deliver rewards or remove items.</p>
     *
     * @param playerId the player's UUID
     * @param questId  the quest ID (config or API quest)
     * @return {@code true} if the operation succeeded, {@code false} if
     *         the player data is not loaded or the quest was already completed
     */
    boolean forceComplete(UUID playerId, String questId);

    /**
     * Resets a quest for the player. Removes the quest from both the
     * completed list and active progress.
     *
     * @param playerId the player's UUID
     * @param questId  the quest ID
     * @return {@code true} if the operation succeeded
     */
    boolean resetQuest(UUID playerId, String questId);

    /**
     * Directly sets the progress value for a specific quest requirement.
     *
     * @param playerId         the player's UUID
     * @param questId          the quest ID
     * @param requirementIndex zero-based index into the quest's requirement list
     * @param value            the progress value to set
     * @return {@code true} if the operation succeeded
     */
    boolean setProgress(UUID playerId, String questId, int requirementIndex, int value);

    /**
     * Resets all quest data for the player — all completed quests,
     * all active progress, and all started quests.
     *
     * <p>Does <b>not</b> affect NPC quests or pool data.</p>
     *
     * @param playerId the player's UUID
     * @return {@code true} if the operation succeeded
     */
    boolean resetAllQuests(UUID playerId);

    // NPC Quests

    /**
     * Force-completes an NPC quest for the player.
     *
     * <p>Does <b>not</b> deliver rewards, remove items, or fire events.</p>
     *
     * @param playerId the player's UUID
     * @param questId  the NPC quest ID
     * @return {@code true} if the operation succeeded
     */
    boolean forceCompleteNpc(UUID playerId, String questId);

    /**
     * Resets an NPC quest for the player. Removes from started, completed,
     * and active NPC progress.
     *
     * @param playerId the player's UUID
     * @param questId  the NPC quest ID
     * @return {@code true} if the operation succeeded
     */
    boolean resetNpcQuest(UUID playerId, String questId);

    /**
     * Resets all NPC quest data for the player.
     *
     * @param playerId the player's UUID
     * @return {@code true} if the operation succeeded
     */
    boolean resetAllNpcQuests(UUID playerId);
}
