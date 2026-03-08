package org.aselstudios.talequests.apiimpl;

import org.aselstudios.talequests.api.event.QuestUnregisteredEvent;
import org.aselstudios.talequests.api.quest.Quest;
import org.aselstudios.talequests.api.quest.QuestRegistry;
import org.aselstudios.talequests.api.quest.Requirement;
import org.aselstudios.talequests.db.QuestDatabase;
import org.aselstudios.talequests.manager.ConfigManager;
import org.aselstudios.talequests.model.QuestModels.PlayerQuestData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QuestRegistryImpl implements QuestRegistry {

    private final QuestDatabase database;
    private volatile EventBusImpl eventBus;
    private final ConcurrentHashMap<String, Quest> quests = new ConcurrentHashMap<>();
    private final Object registryLock = new Object();
    private volatile Snapshot snapshot = new Snapshot(Map.of(), Map.of());

    public QuestRegistryImpl(QuestDatabase database) {
        this.database = database;
    }

    public void setEventBus(EventBusImpl eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void register(Quest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("quest must not be null");
        }
        String id = quest.getId();

        synchronized (registryLock) {
            if (ConfigManager.getQuests().containsKey(id)) {
                throw new IllegalArgumentException(
                        "Quest ID '" + id + "' conflicts with an existing config quest");
            }
            if (ConfigManager.getNpcQuests().containsKey(id)) {
                throw new IllegalArgumentException(
                        "Quest ID '" + id + "' conflicts with an existing NPC quest");
            }
            for (var pool : ConfigManager.getPoolsSorted()) {
                if (pool.quests.containsKey(id)) {
                    throw new IllegalArgumentException(
                            "Quest ID '" + id + "' conflicts with an existing pool quest in pool '" + pool.id + "'");
                }
            }
            if (quests.putIfAbsent(id, quest) != null) {
                throw new IllegalArgumentException(
                        "Quest ID '" + id + "' is already registered in the API registry");
            }

            rebuildSnapshot();
        }
    }

    @Override
    public void unregister(String questId, boolean purgeProgress) {
        if (questId == null) return;

        synchronized (registryLock) {
            Quest removed = quests.remove(questId);
            if (removed == null) return;

            if (purgeProgress) {
                for (Map.Entry<UUID, PlayerQuestData> entry : database.getCachedEntries().entrySet()) {
                    PlayerQuestData data = entry.getValue();
                    synchronized (data) {
                        data.activeProgress.remove(questId);
                        data.completedQuests.remove(questId);
                    }
                    data.markDirty();
                }
            }

            rebuildSnapshot();
        }

        // Fire event outside lock
        if (eventBus != null) {
            eventBus.fire(new QuestUnregisteredEvent(questId, purgeProgress));
        }
    }

    @Override
    public Quest getQuest(String questId) {
        if (questId == null) return null;
        return quests.get(questId);
    }

    @Override
    public Collection<Quest> getAll() {
        return List.copyOf(quests.values());
    }

    @Override
    public boolean isRegistered(String questId) {
        return questId != null && quests.containsKey(questId);
    }

    @Override
    public Collection<Quest> getByCategory(String categoryId) {
        if (categoryId == null) return List.of();
        return snapshot.byCategory.getOrDefault(categoryId, List.of());
    }

    @Override
    public Collection<Quest> getByRequirementType(String type) {
        if (type == null) return List.of();
        return snapshot.byType.getOrDefault(type.toUpperCase(Locale.ROOT), List.of());
    }

    private void rebuildSnapshot() {
        Map<String, List<Quest>> byType = new HashMap<>();
        Map<String, List<Quest>> byCategory = new HashMap<>();

        for (Quest quest : quests.values()) {
            if (quest.getCategory() != null) {
                byCategory.computeIfAbsent(quest.getCategory(), k -> new ArrayList<>()).add(quest);
            }

            Set<String> seenTypes = new HashSet<>();
            for (Requirement req : quest.getRequirements()) {
                if (seenTypes.add(req.getType())) {
                    byType.computeIfAbsent(req.getType(), k -> new ArrayList<>()).add(quest);
                }
            }
        }

        byType.replaceAll((k, v) -> Collections.unmodifiableList(v));
        byCategory.replaceAll((k, v) -> Collections.unmodifiableList(v));

        this.snapshot = new Snapshot(
                Collections.unmodifiableMap(byType),
                Collections.unmodifiableMap(byCategory)
        );
    }

    private record Snapshot(
            Map<String, List<Quest>> byType,
            Map<String, List<Quest>> byCategory
    ) {}
}
