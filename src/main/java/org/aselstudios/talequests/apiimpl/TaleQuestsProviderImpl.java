package org.aselstudios.talequests.apiimpl;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.aselstudios.talequests.api.ProgressResult;
import org.aselstudios.talequests.api.TaleQuestsProvider;
import org.aselstudios.talequests.api.category.CategoryRegistry;
import org.aselstudios.talequests.api.event.EventBus;
import org.aselstudios.talequests.api.extension.CustomRewardHandler;
import org.aselstudios.talequests.api.player.AdminQuestService;
import org.aselstudios.talequests.api.player.NpcQuestService;
import org.aselstudios.talequests.api.player.PlayerDataService;
import org.aselstudios.talequests.api.player.PoolService;
import org.aselstudios.talequests.api.quest.QuestRegistry;
import org.aselstudios.talequests.api.quest.RequirementType;
import org.aselstudios.talequests.db.QuestDatabase;
import org.aselstudios.talequests.listeners.PlayerJoinListener;
import org.aselstudios.talequests.manager.QuestManager;
import org.aselstudios.talequests.model.QuestModels;
import org.aselstudios.talequests.model.QuestModels.PlayerQuestData;
import org.aselstudios.talequests.util.PlayerUtil;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TaleQuestsProviderImpl implements TaleQuestsProvider {

    private final QuestDatabase database;
    private final QuestManager questManager;

    private final QuestRegistryImpl questRegistry;
    private final CategoryRegistryImpl categoryRegistry;
    private final PlayerDataServiceImpl playerDataService;
    private final AdminQuestServiceImpl adminQuestService;
    private final EventBusImpl eventBus;
    private final PoolServiceImpl poolService;
    private final NpcQuestServiceImpl npcQuestService;

    private final ConcurrentHashMap<String, CustomRewardHandler> rewardHandlers = new ConcurrentHashMap<>();

    public TaleQuestsProviderImpl(QuestDatabase database,
                                  QuestManager questManager) {
        this.database = database;
        this.questManager = questManager;

        this.questRegistry = new QuestRegistryImpl(database);
        this.categoryRegistry = new CategoryRegistryImpl();
        this.playerDataService = new PlayerDataServiceImpl(database);
        this.adminQuestService = new AdminQuestServiceImpl(database);
        this.eventBus = new EventBusImpl();
        this.poolService = new PoolServiceImpl(database);
        this.npcQuestService = new NpcQuestServiceImpl();
        this.questRegistry.setEventBus(this.eventBus);
    }

    @Override
    public QuestRegistry getQuestRegistry() { return questRegistry; }

    @Override
    public CategoryRegistry getCategoryRegistry() { return categoryRegistry; }

    @Override
    public PlayerDataService getPlayerDataService() { return playerDataService; }

    @Override
    public AdminQuestService getAdminQuestService() { return adminQuestService; }

    @Override
    public EventBus getEventBus() { return eventBus; }

    @Override
    public PoolService getPoolService() { return poolService; }

    @Override
    public NpcQuestService getNpcQuestService() { return npcQuestService; }

    @Override
    public ProgressResult reportProgress(UUID playerId, String requirementType, String target, int amount) {
        if (playerId == null || requirementType == null || target == null || amount <= 0) {
            return ProgressResult.INVALID_INPUT;
        }

        String normalizedType = requirementType.toUpperCase(Locale.ROOT);

        PlayerRef playerRef = PlayerJoinListener.getOnlinePlayerByUUID(playerId);
        if (playerRef == null) {
            return ProgressResult.PLAYER_OFFLINE;
        }
        Player player = PlayerUtil.getPlayer(playerRef);
        if (player == null) {
            return ProgressResult.PLAYER_OFFLINE;
        }

        if (RequirementType.isBuiltIn(normalizedType)) {
            try {
                QuestModels.RequirementType enumType = QuestModels.RequirementType.valueOf(normalizedType);
                boolean matched = questManager.handleProgress(player, enumType, target, amount);
                return matched ? ProgressResult.OK : ProgressResult.NO_MATCHING_QUESTS;
            } catch (IllegalArgumentException ignored) {
                return ProgressResult.NO_MATCHING_QUESTS;
            }
        }

        PlayerQuestData data = database.load(playerId);
        if (data == null) {
            return ProgressResult.PLAYER_OFFLINE;
        }

        boolean anyUpdated = questManager.handleApiQuestProgress(
                player, playerId, data, normalizedType, target, amount);

        if (anyUpdated) {
            data.markDirty();
        }

        return anyUpdated ? ProgressResult.OK : ProgressResult.NO_MATCHING_QUESTS;
    }

    @Override
    public void registerRewardHandler(String rewardType, CustomRewardHandler handler) {
        if (rewardType == null) throw new IllegalArgumentException("rewardType must not be null");
        if (handler == null) throw new IllegalArgumentException("handler must not be null");
        rewardHandlers.put(rewardType.toUpperCase(Locale.ROOT), handler);
    }

    @Override
    public void unregisterRewardHandler(String rewardType) {
        if (rewardType == null) return;
        rewardHandlers.remove(rewardType.toUpperCase(Locale.ROOT));
    }

    public EventBusImpl getEventBusImpl() { return eventBus; }

    public QuestRegistryImpl getQuestRegistryImpl() { return questRegistry; }

    public CustomRewardHandler getRewardHandler(String type) {
        if (type == null) return null;
        return rewardHandlers.get(type.toUpperCase(Locale.ROOT));
    }
}
