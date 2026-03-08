package org.aselstudios.talequests.api.quest;

import java.util.Collection;

/**
 * Registry for API-registered quests.
 *
 * <p>Config-based quests (defined in YAML files) are <b>not</b> exposed
 * through this registry. This registry only manages quests registered
 * programmatically via the API.</p>
 *
 * <p>Quest IDs must not conflict with config quest IDs, NPC quest IDs,
 * or pool quest IDs. Using a namespace prefix is recommended
 * (e.g. {@code "myplugin:quest_name"}).</p>
 */
public interface QuestRegistry {

    /**
     * Registers a quest.
     *
     * @param quest the quest to register
     * @throws IllegalArgumentException if the quest ID conflicts with an
     *         existing config/NPC/pool/API quest, or if the quest is null
     */
    void register(Quest quest);

    /**
     * Unregisters an API quest by ID.
     *
     * @param questId       the quest ID to unregister
     * @param purgeProgress if {@code true}, removes all player progress data
     *                      for this quest from cached entries; if {@code false},
     *                      orphaned progress data is left in place (harmless)
     */
    void unregister(String questId, boolean purgeProgress);

    /**
     * Returns the API quest with the given ID, or {@code null} if not found.
     */
    Quest getQuest(String questId);

    /**
     * Returns an unmodifiable collection of all API-registered quests.
     */
    Collection<Quest> getAll();

    /**
     * Returns {@code true} if a quest with the given ID is registered in this registry.
     */
    boolean isRegistered(String questId);

    /**
     * Returns all API quests belonging to the given category.
     *
     * @param categoryId the category ID
     * @return unmodifiable collection (empty if none match)
     */
    Collection<Quest> getByCategory(String categoryId);

    /**
     * Returns all API quests that have at least one requirement of the given type.
     * The type string is normalized to UPPER_CASE before matching.
     *
     * @param type the requirement type string
     * @return unmodifiable collection (empty if none match)
     */
    Collection<Quest> getByRequirementType(String type);
}
