package org.aselstudios.talequests.apiimpl;

import org.aselstudios.talequests.api.player.NpcQuestService;
import org.aselstudios.talequests.manager.ConfigManager;
import org.aselstudios.talequests.model.QuestModels.NpcQuest;
import org.aselstudios.talequests.model.QuestModels.NpcQuestChain;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NpcQuestServiceImpl implements NpcQuestService {

    @Override
    public List<String> getNpcQuestIds() {
        Map<String, NpcQuest> quests = ConfigManager.getNpcQuests();
        return List.copyOf(quests.keySet());
    }

    @Override
    @Nullable
    public String getQuestName(String questId) {
        if (questId == null) return null;
        NpcQuest quest = ConfigManager.getNpcQuest(questId);
        return quest != null ? quest.name : null;
    }

    @Override
    @Nullable
    public String getQuestDescription(String questId) {
        if (questId == null) return null;
        NpcQuest quest = ConfigManager.getNpcQuest(questId);
        return quest != null ? quest.description : null;
    }

    @Override
    public int getRequirementCount(String questId) {
        if (questId == null) return -1;
        NpcQuest quest = ConfigManager.getNpcQuest(questId);
        return quest != null ? quest.requirements.size() : -1;
    }

    @Override
    public List<String> getChainIds() {
        Map<String, NpcQuestChain> chains = ConfigManager.getNpcQuestChains();
        return List.copyOf(chains.keySet());
    }

    @Override
    public List<String> getChainQuestIds(String chainId) {
        if (chainId == null) return List.of();
        NpcQuestChain chain = ConfigManager.getNpcQuestChain(chainId);
        if (chain == null || chain.questIds == null) return List.of();
        return List.copyOf(chain.questIds);
    }

    @Override
    public boolean questExists(String questId) {
        if (questId == null) return false;
        return ConfigManager.getNpcQuest(questId) != null;
    }
}
