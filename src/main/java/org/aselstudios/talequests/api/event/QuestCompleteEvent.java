package org.aselstudios.talequests.api.event;

import java.util.UUID;

/**
 * Fired after a quest is completed (both config and API quests).
 *
 * <p>This is a <b>notification event</b> and is <b>not cancellable</b>.
 * The quest has already been marked as completed when this event fires.
 * Use {@link QuestRewardEvent} to cancel reward delivery.</p>
 */
public final class QuestCompleteEvent extends QuestEvent {

    private final UUID playerId;
    private final String questId;
    private final String questName;
    private final boolean apiQuest;

    public QuestCompleteEvent(UUID playerId, String questId, String questName, boolean apiQuest) {
        this.playerId = playerId;
        this.questId = questId;
        this.questName = questName;
        this.apiQuest = apiQuest;
    }

    /** Returns the UUID of the player who completed the quest. */
    public UUID getPlayerId() { return playerId; }

    /** Returns the completed quest's ID. */
    public String getQuestId() { return questId; }

    /** Returns the completed quest's display name. */
    public String getQuestName() { return questName; }

    /**
     * Returns {@code true} if this quest was registered via the API,
     * {@code false} if it's a config-based quest.
     */
    public boolean isApiQuest() { return apiQuest; }
}
