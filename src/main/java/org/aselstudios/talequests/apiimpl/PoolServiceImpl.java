package org.aselstudios.talequests.apiimpl;

import org.aselstudios.talequests.TaleQuests;
import org.aselstudios.talequests.api.player.PoolQuestInfo;
import org.aselstudios.talequests.api.player.PoolService;
import org.aselstudios.talequests.db.QuestDatabase;
import org.aselstudios.talequests.manager.ConfigManager;
import org.aselstudios.talequests.manager.QuestPoolManager;
import org.aselstudios.talequests.model.QuestModels.PlayerPoolData;
import org.aselstudios.talequests.model.QuestModels.PlayerQuestData;
import org.aselstudios.talequests.model.QuestModels.QuestPool;

import javax.annotation.Nullable;
import java.util.*;

public class PoolServiceImpl implements PoolService {

    private final QuestDatabase database;

    public PoolServiceImpl(QuestDatabase database) {
        this.database = database;
    }

    @Override
    public List<String> getPoolIds() {
        Map<String, QuestPool> pools = ConfigManager.getPools();
        return List.copyOf(pools.keySet());
    }

    @Override
    @Nullable
    public String getPoolDisplayName(String poolId) {
        if (poolId == null) return null;
        QuestPool pool = ConfigManager.getPool(poolId);
        return pool != null ? pool.displayName : null;
    }

    @Override
    @Nullable
    public String getResetInterval(String poolId) {
        if (poolId == null) return null;
        QuestPool pool = ConfigManager.getPool(poolId);
        return pool != null ? pool.resetInterval.name() : null;
    }

    @Override
    public long getTimeRemainingMs(String poolId) {
        if (poolId == null) return -1;
        QuestPool pool = ConfigManager.getPool(poolId);
        if (pool == null) return -1;
        QuestPoolManager pm = TaleQuests.POOL_MANAGER;
        if (pm == null) return -1;
        return pm.getTimeRemainingMs(pool.resetInterval);
    }

    @Override
    @Nullable
    public String getFormattedTimeRemaining(String poolId) {
        long remaining = getTimeRemainingMs(poolId);
        if (remaining < 0) return null;
        QuestPoolManager pm = TaleQuests.POOL_MANAGER;
        if (pm == null) return null;
        return pm.formatTimeRemaining(remaining);
    }

    @Override
    @Nullable
    public PoolQuestInfo getPlayerPoolInfo(UUID playerId, String poolId) {
        if (playerId == null || poolId == null) return null;
        if (!poolExists(poolId)) return null;

        PlayerQuestData data = database.getCachedEntries().get(playerId);
        if (data == null) return null;

        PlayerPoolData poolData = data.poolData.get(poolId);
        if (poolData == null) return null;

        PlayerPoolData copy = poolData.copy();
        return new PoolQuestInfo(
                poolId,
                copy.cycleStartEpochMs,
                copy.assignedQuestIds,
                copy.completedPoolQuests,
                copy.poolProgress
        );
    }

    @Override
    public boolean poolExists(String poolId) {
        if (poolId == null) return false;
        return ConfigManager.getPool(poolId) != null;
    }
}
