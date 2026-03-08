# Player Data

The `PlayerDataService` provides read-only access to player quest states. All returned data is an immutable snapshot, safe to use from any thread.

## Getting Player Data

```java
import org.aselstudios.talequests.api.player.PlayerDataService;
import org.aselstudios.talequests.api.player.QuestPlayer;

TaleQuestsProvider api = TaleQuestsAPI.get();
PlayerDataService dataService = api.getPlayerDataService();

// Check if player data is loaded
boolean loaded = dataService.isLoaded(playerId);

// Get a snapshot of the player's quest data
QuestPlayer player = dataService.getPlayer(playerId);
if (player == null) {
    // Player is offline or data hasn't loaded yet
    return;
}
```

**Important:** `getPlayer()` returns `null` if the player is offline or their data is not yet loaded. Always null-check the result.

## Quest Status

The `QuestStatus` enum represents a player's relationship to a quest:

| Status | Description |
|--------|-------------|
| `NOT_STARTED` | Player has no progress on this quest |
| `IN_PROGRESS` | Player has started but not completed the quest |
| `COMPLETED` | Player has finished the quest |
| `LOCKED` | Prerequisites are not met (only with prerequisite-aware check) |

### Simple Status Check

```java
import org.aselstudios.talequests.api.player.QuestStatus;

QuestPlayer player = api.getPlayerDataService().getPlayer(playerId);
if (player == null) return;

QuestStatus status = player.getQuestStatus("myplugin:iron_miner");

switch (status) {
    case NOT_STARTED -> System.out.println("Quest not started yet");
    case IN_PROGRESS -> System.out.println("Quest is active");
    case COMPLETED   -> System.out.println("Quest is done!");
    default          -> {}
}
```

### Status with Prerequisite Checking

Pass a list of prerequisite quest IDs to get `LOCKED` status when prerequisites are not met:

```java
List<String> prerequisites = List.of("myplugin:basic_mining", "myplugin:gathering_101");

QuestStatus status = player.getQuestStatus("myplugin:advanced_mining", prerequisites);

if (status == QuestStatus.LOCKED) {
    System.out.println("Player needs to complete prerequisites first!");
}
```

## Completion and Progress Checks

### Boolean Checks

```java
QuestPlayer player = api.getPlayerDataService().getPlayer(playerId);
if (player == null) return;

// Check if a quest is completed
boolean done = player.hasCompleted("myplugin:iron_miner");

// Check if a quest is currently in progress
boolean active = player.isInProgress("myplugin:iron_miner");
```

### Progress Values

```java
// Get progress on a specific requirement (by index)
int progress = player.getProgress("myplugin:iron_miner", 0); // First requirement
int progress2 = player.getProgress("myplugin:iron_miner", 1); // Second requirement

// Get all progress for a quest (map of requirement index to progress value)
Map<Integer, Integer> allProgress = player.getAllProgress("myplugin:iron_miner");
for (Map.Entry<Integer, Integer> entry : allProgress.entrySet()) {
    System.out.println("Requirement " + entry.getKey() + ": " + entry.getValue());
}
```

### Quest Lists

```java
// All completed quest IDs (both API and YAML)
List<String> completed = player.getCompletedQuests();

// All started (in-progress) quest IDs
List<String> started = player.getStartedQuests();

// All quests with active progress (IDs only)
Set<String> active = player.getActiveQuestIds();
```

## NPC Quest Data

The `QuestPlayer` snapshot also contains NPC quest data:

```java
QuestPlayer player = api.getPlayerDataService().getPlayer(playerId);
if (player == null) return;

// Check NPC quest completion
boolean npcDone = player.hasCompletedNpc("npc_quest_1");

// Check if NPC quest is started
boolean npcStarted = player.hasStartedNpc("npc_quest_1");

// Get NPC quest progress
int npcProgress = player.getNpcProgress("npc_quest_1", 0);

// List completed and started NPC quests
List<String> completedNpc = player.getCompletedNpcQuests();
List<String> startedNpc = player.getStartedNpcQuests();
```

## Pool Quest Data

Player pool data is available through `PoolQuestInfo` snapshots:

```java
import org.aselstudios.talequests.api.player.PoolQuestInfo;

QuestPlayer player = api.getPlayerDataService().getPlayer(playerId);
if (player == null) return;

// Get pool IDs the player has data for
Set<String> poolIds = player.getPoolIds();

// Get data for a specific pool
PoolQuestInfo poolInfo = player.getPoolInfo("daily_quests");
if (poolInfo != null) {
    // Pool metadata
    String poolId = poolInfo.getPoolId();
    long cycleStart = poolInfo.getCycleStartEpochMs();

    // Quest assignments for this cycle
    List<String> assigned = poolInfo.getAssignedQuestIds();
    List<String> completed = poolInfo.getCompletedQuestIds();

    // Check if a specific pool quest is completed
    boolean questDone = poolInfo.hasCompleted("pool_quest_3");

    // Get progress on a specific pool quest requirement
    int progress = poolInfo.getProgress("pool_quest_3", 0);
}
```

## Practical Examples

### Condition Check for Custom Mechanic

```java
/**
 * Only allow access to an area if the player has completed a quest.
 */
public boolean canEnterDungeon(UUID playerId) {
    QuestPlayer player = TaleQuestsAPI.get().getPlayerDataService().getPlayer(playerId);
    if (player == null) return false;
    return player.hasCompleted("myplugin:dungeon_key");
}
```

### Display Quest Summary

```java
/**
 * Show a summary of a player's quest progress.
 */
public void showSummary(UUID playerId) {
    QuestPlayer player = TaleQuestsAPI.get().getPlayerDataService().getPlayer(playerId);
    if (player == null) {
        System.out.println("Player data not available.");
        return;
    }

    System.out.println("Completed quests: " + player.getCompletedQuests().size());
    System.out.println("Active quests: " + player.getStartedQuests().size());
    System.out.println("Completed NPC quests: " + player.getCompletedNpcQuests().size());

    for (String poolId : player.getPoolIds()) {
        PoolQuestInfo info = player.getPoolInfo(poolId);
        if (info != null) {
            System.out.println("Pool " + poolId + ": " +
                info.getCompletedQuestIds().size() + "/" +
                info.getAssignedQuestIds().size() + " done");
        }
    }
}
```

### Prerequisite-Aware Quest Board

```java
/**
 * Build a quest board showing status for each quest.
 */
public void showQuestBoard(UUID playerId, List<Quest> quests) {
    QuestPlayer player = TaleQuestsAPI.get().getPlayerDataService().getPlayer(playerId);
    if (player == null) return;

    for (Quest quest : quests) {
        QuestStatus status = player.getQuestStatus(
            quest.getId(),
            quest.getPrerequisiteIds()
        );

        String statusText = switch (status) {
            case COMPLETED   -> "[DONE]";
            case IN_PROGRESS -> "[ACTIVE]";
            case LOCKED      -> "[LOCKED]";
            case NOT_STARTED -> "[AVAILABLE]";
        };

        System.out.println(statusText + " " + quest.getName());
    }
}
```
