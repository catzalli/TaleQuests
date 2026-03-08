package org.aselstudios.talequests.api.player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A read-only snapshot of a player's pool quest data for a specific pool.
 */
public final class PoolQuestInfo {

    private final String poolId;
    private final long cycleStartEpochMs;
    private final List<String> assignedQuestIds;
    private final List<String> completedQuestIds;
    private final Map<String, Map<Integer, Integer>> progress;

    public PoolQuestInfo(String poolId, long cycleStartEpochMs,
                         List<String> assignedQuestIds,
                         List<String> completedQuestIds,
                         Map<String, Map<Integer, Integer>> progress) {
        this.poolId = poolId;
        this.cycleStartEpochMs = cycleStartEpochMs;
        this.assignedQuestIds = List.copyOf(assignedQuestIds);
        this.completedQuestIds = List.copyOf(completedQuestIds);
        this.progress = deepCopyProgress(progress);
    }

    /** Returns the pool ID. */
    public String getPoolId() { return poolId; }

    /** Returns the cycle start time in epoch milliseconds. */
    public long getCycleStartEpochMs() { return cycleStartEpochMs; }

    /** Returns an unmodifiable list of quest IDs assigned to this player for this pool. */
    public List<String> getAssignedQuestIds() { return assignedQuestIds; }

    /** Returns an unmodifiable list of completed quest IDs in this pool for this cycle. */
    public List<String> getCompletedQuestIds() { return completedQuestIds; }

    /** Returns {@code true} if the player has completed the given pool quest. */
    public boolean hasCompleted(String questId) {
        return completedQuestIds.contains(questId);
    }

    /**
     * Returns the progress for a specific pool quest requirement.
     *
     * @param questId          the pool quest ID
     * @param requirementIndex zero-based index
     * @return the current progress, or 0 if not tracked
     */
    public int getProgress(String questId, int requirementIndex) {
        Map<Integer, Integer> questProgress = progress.get(questId);
        if (questProgress == null) return 0;
        return questProgress.getOrDefault(requirementIndex, 0);
    }

    private static Map<String, Map<Integer, Integer>> deepCopyProgress(
            Map<String, Map<Integer, Integer>> source) {
        if (source == null || source.isEmpty()) return Map.of();
        Map<String, Map<Integer, Integer>> copy = new HashMap<>(source.size());
        for (var entry : source.entrySet()) {
            copy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }
}
