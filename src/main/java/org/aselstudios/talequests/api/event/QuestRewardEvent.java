package org.aselstudios.talequests.api.event;

import org.aselstudios.talequests.api.quest.Reward;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Fired <b>before</b> quest rewards are delivered.
 *
 * <p>This event is <b>cancellable</b>. If cancelled, all reward
 * delivery is skipped. The quest itself has already been marked as
 * completed when this event fires.</p>
 */
public final class QuestRewardEvent extends QuestEvent {

    private final UUID playerId;
    private final String questId;
    private final List<Reward> rewards;
    private final boolean apiQuest;
    private boolean cancelled;

    public QuestRewardEvent(UUID playerId, String questId, List<Reward> rewards, boolean apiQuest) {
        this.playerId = playerId;
        this.questId = questId;
        this.rewards = List.copyOf(rewards);
        this.apiQuest = apiQuest;
    }

    /** Returns the UUID of the player receiving rewards. */
    public UUID getPlayerId() { return playerId; }

    /** Returns the quest ID. */
    public String getQuestId() { return questId; }

    /** Returns an unmodifiable list of rewards that will be delivered. */
    public List<Reward> getRewards() { return rewards; }

    /**
     * Returns {@code true} if this quest was registered via the API,
     * {@code false} if it's a config-based quest.
     */
    public boolean isApiQuest() { return apiQuest; }

    /** Returns {@code true} if this event has been cancelled. */
    public boolean isCancelled() { return cancelled; }

    /** Sets whether this event is cancelled. If cancelled, no rewards will be delivered. */
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
