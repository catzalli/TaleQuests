package org.aselstudios.talequests.api.player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A read-only snapshot of a player's quest state at a point in time.
 *
 * <p>This covers both config quests and API quests, since they share
 * the same underlying progress data.</p>
 *
 * <p>Instances are obtained via {@link PlayerDataService#getPlayer(UUID)}.</p>
 */
public final class QuestPlayer {

    private final UUID playerId;
    private final List<String> completedQuests;
    private final List<String> startedQuests;
    private final Map<String, Map<Integer, Integer>> activeProgress;
    private final List<String> npcCompletedQuests;
    private final List<String> npcStartedQuests;
    private final Map<String, Map<Integer, Integer>> npcActiveProgress;
    private final Map<String, PoolQuestInfo> poolData;

    public QuestPlayer(UUID playerId,
                       List<String> completedQuests,
                       List<String> startedQuests,
                       Map<String, Map<Integer, Integer>> activeProgress,
                       List<String> npcCompletedQuests,
                       List<String> npcStartedQuests,
                       Map<String, Map<Integer, Integer>> npcActiveProgress,
                       Map<String, PoolQuestInfo> poolData) {
        this.playerId = playerId;
        this.completedQuests = List.copyOf(completedQuests);
        this.startedQuests = List.copyOf(startedQuests);
        this.activeProgress = deepCopyProgress(activeProgress);
        this.npcCompletedQuests = List.copyOf(npcCompletedQuests);
        this.npcStartedQuests = List.copyOf(npcStartedQuests);
        this.npcActiveProgress = deepCopyProgress(npcActiveProgress);
        this.poolData = poolData != null ? Map.copyOf(poolData) : Map.of();
    }

    /** Returns the player's UUID. */
    public UUID getPlayerId() { return playerId; }

    /** Returns {@code true} if the player has completed the given quest. */
    public boolean hasCompleted(String questId) {
        return completedQuests.contains(questId);
    }

    /** Returns {@code true} if the player has active progress for the given quest. */
    public boolean isInProgress(String questId) {
        return activeProgress.containsKey(questId);
    }

    /**
     * Returns the status of a quest for this player.
     *
     * <p>Note: This method cannot check prerequisites (it doesn't know
     * quest definitions). It returns {@link QuestStatus#LOCKED} only
     * if the quest is known to be in the started list but has no progress.
     * For prerequisite-aware status checking, use the overload that
     * accepts prerequisite IDs.</p>
     *
     * @param questId the quest ID
     * @return the quest status
     */
    public QuestStatus getQuestStatus(String questId) {
        if (completedQuests.contains(questId)) return QuestStatus.COMPLETED;
        if (activeProgress.containsKey(questId)) return QuestStatus.IN_PROGRESS;
        return QuestStatus.NOT_STARTED;
    }

    /**
     * Returns the status of a quest for this player, considering prerequisites.
     *
     * @param questId         the quest ID
     * @param prerequisiteIds the quest's prerequisite IDs (empty if none)
     * @return the quest status ({@link QuestStatus#LOCKED} if prerequisites not met)
     */
    public QuestStatus getQuestStatus(String questId, List<String> prerequisiteIds) {
        if (completedQuests.contains(questId)) return QuestStatus.COMPLETED;
        if (activeProgress.containsKey(questId)) return QuestStatus.IN_PROGRESS;
        if (prerequisiteIds != null && !prerequisiteIds.isEmpty()) {
            for (String prereq : prerequisiteIds) {
                if (!completedQuests.contains(prereq)) return QuestStatus.LOCKED;
            }
        }
        return QuestStatus.NOT_STARTED;
    }

    /**
     * Returns the player's progress for a specific requirement of a quest.
     *
     * @param questId          the quest ID
     * @param requirementIndex zero-based index into the quest's requirement list
     * @return the current progress value, or 0 if not tracked
     */
    public int getProgress(String questId, int requirementIndex) {
        Map<Integer, Integer> progress = activeProgress.get(questId);
        if (progress == null) return 0;
        return progress.getOrDefault(requirementIndex, 0);
    }

    /**
     * Returns the full progress map for a quest, or an empty map if no progress exists.
     *
     * @param questId the quest ID
     * @return unmodifiable map of requirement index to progress value
     */
    public Map<Integer, Integer> getAllProgress(String questId) {
        Map<Integer, Integer> progress = activeProgress.get(questId);
        return progress != null ? progress : Map.of();
    }

    /** Returns an unmodifiable list of completed quest IDs. */
    public List<String> getCompletedQuests() { return completedQuests; }

    /** Returns an unmodifiable list of started quest IDs. */
    public List<String> getStartedQuests() { return startedQuests; }

    /** Returns an unmodifiable set of quest IDs that have active progress. */
    public Set<String> getActiveQuestIds() { return activeProgress.keySet(); }

    // NPC Quests

    /** Returns {@code true} if the player has completed the given NPC quest. */
    public boolean hasCompletedNpc(String questId) {
        return npcCompletedQuests.contains(questId);
    }

    /** Returns {@code true} if the player has started the given NPC quest. */
    public boolean hasStartedNpc(String questId) {
        return npcStartedQuests.contains(questId);
    }

    /**
     * Returns the player's progress for a specific NPC quest requirement.
     *
     * @param questId          the NPC quest ID
     * @param requirementIndex zero-based index into the quest's requirement list
     * @return the current progress value, or 0 if not tracked
     */
    public int getNpcProgress(String questId, int requirementIndex) {
        Map<Integer, Integer> progress = npcActiveProgress.get(questId);
        if (progress == null) return 0;
        return progress.getOrDefault(requirementIndex, 0);
    }

    /** Returns an unmodifiable list of completed NPC quest IDs. */
    public List<String> getCompletedNpcQuests() { return npcCompletedQuests; }

    /** Returns an unmodifiable list of started NPC quest IDs. */
    public List<String> getStartedNpcQuests() { return npcStartedQuests; }

    // Pool Quests

    /**
     * Returns the pool quest info for a specific pool,
     * or {@code null} if no data exists for that pool.
     */
    public PoolQuestInfo getPoolInfo(String poolId) {
        return poolData.get(poolId);
    }

    /** Returns an unmodifiable set of pool IDs that have data for this player. */
    public Set<String> getPoolIds() { return poolData.keySet(); }

    /** Deep-copies a progress map so callers cannot mutate internal state. */
    private static Map<String, Map<Integer, Integer>> deepCopyProgress(
            Map<String, Map<Integer, Integer>> source) {
        Map<String, Map<Integer, Integer>> copy = new HashMap<>(source.size());
        for (var entry : source.entrySet()) {
            copy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }
}
