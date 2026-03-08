package org.aselstudios.talequests.apiimpl;

import org.aselstudios.talequests.api.player.PlayerDataService;
import org.aselstudios.talequests.api.player.PoolQuestInfo;
import org.aselstudios.talequests.api.player.QuestPlayer;
import org.aselstudios.talequests.db.QuestDatabase;
import org.aselstudios.talequests.model.QuestModels.PlayerPoolData;
import org.aselstudios.talequests.model.QuestModels.PlayerQuestData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataServiceImpl implements PlayerDataService {

    private final QuestDatabase database;

    public PlayerDataServiceImpl(QuestDatabase database) {
        this.database = database;
    }

    @Override
    public QuestPlayer getPlayer(UUID playerId) {
        if (playerId == null) return null;
        if (!isLoaded(playerId)) return null;

        PlayerQuestData data = database.load(playerId);
        if (data == null) return null;

        PlayerQuestData copy = data.copy();

        Map<String, Map<Integer, Integer>> progressCopy = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Integer>> entry : copy.activeProgress.entrySet()) {
            progressCopy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }

        Map<String, Map<Integer, Integer>> npcProgressCopy = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Integer>> entry : copy.npcActiveProgress.entrySet()) {
            npcProgressCopy.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }

        Map<String, PoolQuestInfo> poolInfoCopy = new HashMap<>();
        for (Map.Entry<String, PlayerPoolData> poolEntry : copy.poolData.entrySet()) {
            PlayerPoolData pd = poolEntry.getValue();
            poolInfoCopy.put(poolEntry.getKey(), new PoolQuestInfo(
                    poolEntry.getKey(),
                    pd.cycleStartEpochMs,
                    pd.assignedQuestIds,
                    pd.completedPoolQuests,
                    pd.poolProgress
            ));
        }

        return new QuestPlayer(
                playerId,
                copy.completedQuests,
                copy.startedQuests,
                progressCopy,
                copy.npcCompletedQuests,
                copy.npcStartedQuests,
                npcProgressCopy,
                poolInfoCopy
        );
    }

    @Override
    public boolean isLoaded(UUID playerId) {
        if (playerId == null) return false;
        return database.getCachedEntries().containsKey(playerId);
    }
}
