package org.aselstudios.talequests.api.event;

/**
 * Fired when a quest is unregistered from the API quest registry.
 *
 * <p>This is a <b>notification event</b> and is <b>not cancellable</b>.
 * The quest has already been removed when this event fires.</p>
 */
public final class QuestUnregisteredEvent extends QuestEvent {

    private final String questId;
    private final boolean progressPurged;

    public QuestUnregisteredEvent(String questId, boolean progressPurged) {
        this.questId = questId;
        this.progressPurged = progressPurged;
    }

    /** Returns the ID of the quest that was unregistered. */
    public String getQuestId() { return questId; }

    /** Returns {@code true} if player progress data was purged during unregistration. */
    public boolean isProgressPurged() { return progressPurged; }
}
