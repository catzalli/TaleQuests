package org.aselstudios.talequests.api.event;

import java.util.UUID;

/**
 * Fired <b>before</b> quest progress is updated.
 *
 * <p>This event is <b>cancellable</b>. If cancelled, the progress
 * value will not be updated. This is safe because the player's
 * progress stays at its old value (no infinite re-trigger risk).</p>
 */
public final class QuestProgressEvent extends QuestEvent {

    private final UUID playerId;
    private final String questId;
    private final String requirementType;
    private final String target;
    private final int requirementIndex;
    private final int oldProgress;
    private final int newProgress;
    private final int required;
    private final boolean apiQuest;
    private boolean cancelled;

    public QuestProgressEvent(UUID playerId, String questId,
                              String requirementType, String target,
                              int requirementIndex,
                              int oldProgress, int newProgress, int required,
                              boolean apiQuest) {
        this.playerId = playerId;
        this.questId = questId;
        this.requirementType = requirementType;
        this.target = target;
        this.requirementIndex = requirementIndex;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
        this.required = required;
        this.apiQuest = apiQuest;
    }

    /** Returns the UUID of the player whose progress changed. */
    public UUID getPlayerId() { return playerId; }

    /** Returns the quest ID. */
    public String getQuestId() { return questId; }

    /** Returns the requirement type (always UPPER_CASE). */
    public String getRequirementType() { return requirementType; }

    /** Returns the target identifier. */
    public String getTarget() { return target; }

    /** Returns the zero-based index of the requirement in the quest's requirement list. */
    public int getRequirementIndex() { return requirementIndex; }

    /** Returns the progress value before this update. */
    public int getOldProgress() { return oldProgress; }

    /** Returns the progress value that will be set (if not cancelled). */
    public int getNewProgress() { return newProgress; }

    /** Returns the total amount required for this requirement. */
    public int getRequired() { return required; }

    /**
     * Returns {@code true} if this quest was registered via the API,
     * {@code false} if it's a config-based quest.
     */
    public boolean isApiQuest() { return apiQuest; }

    /** Returns {@code true} if this event has been cancelled. */
    public boolean isCancelled() { return cancelled; }

    /** Sets whether this event is cancelled. If cancelled, progress will not be updated. */
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
