package org.aselstudios.talequests.api.event;

import java.util.UUID;

/**
 * Fired when a player first begins working on a quest (first progress recorded).
 *
 * <p>This event fires once per quest per player — when a quest transitions
 * from "not started" to "in progress" (i.e., when the first progress entry
 * is created for that quest).</p>
 *
 * <p>This is a <b>notification event</b> and is <b>not cancellable</b>.
 * To prevent progress, cancel the {@link QuestProgressEvent} instead.</p>
 */
public final class QuestStartEvent extends QuestEvent {

    private final UUID playerId;
    private final String questId;
    private final String questName;
    private final boolean apiQuest;

    public QuestStartEvent(UUID playerId, String questId, String questName, boolean apiQuest) {
        this.playerId = playerId;
        this.questId = questId;
        this.questName = questName;
        this.apiQuest = apiQuest;
    }

    /** Returns the UUID of the player who started the quest. */
    public UUID getPlayerId() { return playerId; }

    /** Returns the quest ID. */
    public String getQuestId() { return questId; }

    /** Returns the quest display name. */
    public String getQuestName() { return questName; }

    /**
     * Returns {@code true} if this quest was registered via the API,
     * {@code false} if it's a config-based quest.
     */
    public boolean isApiQuest() { return apiQuest; }
}
