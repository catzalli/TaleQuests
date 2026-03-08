package org.aselstudios.talequests.api;

import org.aselstudios.talequests.api.category.CategoryRegistry;
import org.aselstudios.talequests.api.event.EventBus;
import org.aselstudios.talequests.api.extension.CustomRewardHandler;
import org.aselstudios.talequests.api.player.AdminQuestService;
import org.aselstudios.talequests.api.player.NpcQuestService;
import org.aselstudios.talequests.api.player.PlayerDataService;
import org.aselstudios.talequests.api.player.PoolService;
import org.aselstudios.talequests.api.quest.QuestRegistry;

import java.util.UUID;

/**
 * Main provider interface for the TaleQuests API.
 * Obtain an instance via {@link TaleQuestsAPI#get()}.
 */
public interface TaleQuestsProvider {

    /**
     * Returns the quest registry for registering and querying API quests.
     */
    QuestRegistry getQuestRegistry();

    /**
     * Returns the category registry for registering and querying API categories.
     */
    CategoryRegistry getCategoryRegistry();

    /**
     * Returns the read-only player data service.
     */
    PlayerDataService getPlayerDataService();

    /**
     * Returns the admin quest service for force-completing and resetting quests.
     * These operations bypass normal quest flow (no rewards, no item removal, no events).
     */
    AdminQuestService getAdminQuestService();

    /**
     * Returns the event bus for subscribing to quest events.
     */
    EventBus getEventBus();

    /**
     * Returns the pool service for querying quest pool state.
     */
    PoolService getPoolService();

    /**
     * Returns the NPC quest service for querying NPC quest definitions.
     */
    NpcQuestService getNpcQuestService();

    /**
     * Reports progress for API-registered quests.
     *
     * <p>This method only processes quests registered via the API.
     * Config-based quests are handled automatically by built-in listeners.</p>
     *
     * <p>The player must be online. All type strings are normalized to
     * {@code UPPER_CASE} internally.</p>
     *
     * @param playerId        the player's UUID
     * @param requirementType the requirement type (e.g. {@code "KILL_MOB"} or a custom type like {@code "FISHING"})
     * @param target          the target identifier (e.g. mob ID, item ID, block ID)
     * @param amount          the amount of progress to add (must be positive)
     * @return the result of the operation
     */
    ProgressResult reportProgress(UUID playerId, String requirementType, String target, int amount);

    /**
     * Registers a handler for a custom reward type. When a quest with this
     * reward type is completed, the handler will be called to deliver the reward.
     *
     * <p>This works for both API-registered quests (where the reward type string
     * directly matches, e.g. {@code .reward("XP", "50", "50 XP")}) and YAML
     * config quests (where the reward uses {@code type: CUSTOM} with a matching
     * {@code custom_reward_type} field).</p>
     *
     * <p>Type strings are normalized to {@code UPPER_CASE} internally.</p>
     *
     * @param rewardType the custom reward type (e.g. {@code "XP"}, {@code "SKILL_POINTS"})
     * @param handler    the handler that delivers the reward
     */
    void registerRewardHandler(String rewardType, CustomRewardHandler handler);

    /**
     * Unregisters a previously registered custom reward handler.
     *
     * @param rewardType the reward type to unregister
     */
    void unregisterRewardHandler(String rewardType);
}
