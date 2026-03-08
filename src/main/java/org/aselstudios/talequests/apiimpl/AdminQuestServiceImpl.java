package org.aselstudios.talequests.apiimpl;

import org.aselstudios.talequests.api.player.AdminQuestService;
import org.aselstudios.talequests.db.QuestDatabase;
import org.aselstudios.talequests.model.QuestModels.PlayerQuestData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AdminQuestServiceImpl implements AdminQuestService {

    private final QuestDatabase database;

    public AdminQuestServiceImpl(QuestDatabase database) {
        this.database = database;
    }

    // ── Regular Quests ──

    @Override
    public boolean forceComplete(UUID playerId, String questId) {
        if (playerId == null || questId == null) return false;
        PlayerQuestData data = database.getCachedEntries().get(playerId);
        if (data == null) return false;

        synchronized (data) {
            if (data.completedQuests.contains(questId)) return false;
            data.completedQuests.add(questId);
            data.activeProgress.remove(questId);
        }
        data.markDirty();
        return true;
    }

    @Override
    public boolean resetQuest(UUID playerId, String questId) {
        if (playerId == null || questId == null) return false;
        PlayerQuestData data = database.getCachedEntries().get(playerId);
        if (data == null) return false;

        synchronized (data) {
            data.completedQuests.remove(questId);
            data.activeProgress.remove(questId);
            data.startedQuests.remove(questId);
        }
        data.markDirty();
        return true;
    }

    @Override
    public boolean setProgress(UUID playerId, String questId, int requirementIndex, int value) {
        if (playerId == null || questId == null || requirementIndex < 0 || requirementIndex > 99 || value < 0) return false;
        PlayerQuestData data = database.getCachedEntries().get(playerId);
        if (data == null) return false;

        synchronized (data) {
            if (data.completedQuests.contains(questId)) return false;
            Map<Integer, Integer> progress = data.activeProgress.computeIfAbsent(
                    questId, k -> new ConcurrentHashMap<>());
            progress.put(requirementIndex, value);
        }
        data.markDirty();
        return true;
    }

    @Override
    public boolean resetAllQuests(UUID playerId) {
        if (playerId == null) return false;
        PlayerQuestData data = database.getCachedEntries().get(playerId);
        if (data == null) return false;

        synchronized (data) {
            data.completedQuests.clear();
            data.activeProgress.clear();
            data.startedQuests.clear();
        }
        data.markDirty();
        return true;
    }

    @Override
    public boolean forceCompleteNpc(UUID playerId, String questId) {
        if (playerId == null || questId == null) return false;
        PlayerQuestData data = database.getCachedEntries().get(playerId);
        if (data == null) return false;

        synchronized (data) {
            if (data.npcCompletedQuests.contains(questId)) return false;
            data.npcCompletedQuests.add(questId);
            data.npcActiveProgress.remove(questId);
            if (!data.npcStartedQuests.contains(questId)) {
                data.npcStartedQuests.add(questId);
            }
        }
        data.markDirty();
        return true;
    }

    @Override
    public boolean resetNpcQuest(UUID playerId, String questId) {
        if (playerId == null || questId == null) return false;
        PlayerQuestData data = database.getCachedEntries().get(playerId);
        if (data == null) return false;

        synchronized (data) {
            data.npcStartedQuests.remove(questId);
            data.npcCompletedQuests.remove(questId);
            data.npcActiveProgress.remove(questId);
        }
        data.markDirty();
        return true;
    }

    @Override
    public boolean resetAllNpcQuests(UUID playerId) {
        if (playerId == null) return false;
        PlayerQuestData data = database.getCachedEntries().get(playerId);
        if (data == null) return false;

        synchronized (data) {
            data.npcStartedQuests.clear();
            data.npcCompletedQuests.clear();
            data.npcActiveProgress.clear();
        }
        data.markDirty();
        return true;
    }
}
