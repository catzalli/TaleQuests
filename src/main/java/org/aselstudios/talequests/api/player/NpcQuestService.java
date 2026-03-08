package org.aselstudios.talequests.api.player;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Read-only access to NPC quest definitions from config.
 *
 * <p>NPC quests are quests that are started and progressed through
 * NPC interactions, defined in YAML config. This service provides
 * read-only access to their definitions.</p>
 */
public interface NpcQuestService {

    /**
     * Returns a list of all configured NPC quest IDs.
     */
    List<String> getNpcQuestIds();

    /**
     * Returns the display name of an NPC quest, or {@code null} if it doesn't exist.
     */
    @Nullable
    String getQuestName(String questId);

    /**
     * Returns the description of an NPC quest, or {@code null} if it doesn't exist.
     */
    @Nullable
    String getQuestDescription(String questId);

    /**
     * Returns the number of requirements for an NPC quest, or {@code -1} if it doesn't exist.
     */
    int getRequirementCount(String questId);

    /**
     * Returns a list of all NPC quest chain IDs.
     */
    List<String> getChainIds();

    /**
     * Returns the list of quest IDs in a chain (in order), or an empty list if the chain doesn't exist.
     */
    List<String> getChainQuestIds(String chainId);

    /**
     * Returns {@code true} if an NPC quest with the given ID exists.
     */
    boolean questExists(String questId);
}
