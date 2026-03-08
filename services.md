# Services

TaleQuests provides three service interfaces for querying pool data, NPC quest definitions, and performing administrative operations.

## PoolService

The `PoolService` provides read-only access to quest pool configurations and player pool states. Quest pools are rotating quest sets (daily, weekly) that reset on a schedule.

```java
import org.aselstudios.talequests.api.player.PoolService;
import org.aselstudios.talequests.api.player.PoolQuestInfo;

TaleQuestsProvider api = TaleQuestsAPI.get();
PoolService poolService = api.getPoolService();
```

### Querying Pool Configuration

```java
// List all configured pool IDs
List<String> poolIds = poolService.getPoolIds();

// Check if a pool exists
boolean exists = poolService.poolExists("daily_quests");

// Get pool display name
String name = poolService.getPoolDisplayName("daily_quests");
// Returns null if pool doesn't exist

// Get reset interval ("DAILY" or "WEEKLY")
String interval = poolService.getResetInterval("daily_quests");

// Get time remaining until next reset (milliseconds)
long msRemaining = poolService.getTimeRemainingMs("daily_quests");
// Returns -1 if pool doesn't exist

// Get human-readable time remaining (e.g. "2h 15m")
String timeLeft = poolService.getFormattedTimeRemaining("daily_quests");
// Returns null if pool doesn't exist
```

### Querying Player Pool Data

```java
// Get a player's data for a specific pool
PoolQuestInfo info = poolService.getPlayerPoolInfo(playerId, "daily_quests");

if (info != null) {
    // Which quests were assigned this cycle
    List<String> assigned = info.getAssignedQuestIds();

    // Which quests the player has completed this cycle
    List<String> completed = info.getCompletedQuestIds();

    // Check a specific quest
    boolean done = info.hasCompleted("daily_mine_stone");

    // Get progress on a requirement
    int progress = info.getProgress("daily_mine_stone", 0);

    // Cycle start timestamp
    long cycleStart = info.getCycleStartEpochMs();
}
```

### Pool Dashboard Example

```java
public void showPoolDashboard(UUID playerId) {
    PoolService poolService = TaleQuestsAPI.get().getPoolService();

    for (String poolId : poolService.getPoolIds()) {
        String name = poolService.getPoolDisplayName(poolId);
        String interval = poolService.getResetInterval(poolId);
        String timeLeft = poolService.getFormattedTimeRemaining(poolId);

        System.out.println(name + " (" + interval + ")");
        System.out.println("  Resets in: " + timeLeft);

        PoolQuestInfo info = poolService.getPlayerPoolInfo(playerId, poolId);
        if (info != null) {
            int done = info.getCompletedQuestIds().size();
            int total = info.getAssignedQuestIds().size();
            System.out.println("  Progress: " + done + "/" + total + " completed");
        } else {
            System.out.println("  No data (player may not have logged in this cycle)");
        }
    }
}
```

## NpcQuestService

The `NpcQuestService` provides read-only access to NPC quest definitions and chain configurations. NPC quests are special quests triggered by interacting with NPCs or via commands.

```java
import org.aselstudios.talequests.api.player.NpcQuestService;

TaleQuestsProvider api = TaleQuestsAPI.get();
NpcQuestService npcService = api.getNpcQuestService();
```

### Querying NPC Quests

```java
// List all NPC quest IDs
List<String> questIds = npcService.getNpcQuestIds();

// Check if a quest exists
boolean exists = npcService.questExists("blacksmith_delivery");

// Get quest display name
String name = npcService.getQuestName("blacksmith_delivery");
// Returns null if quest doesn't exist

// Get quest description
String desc = npcService.getQuestDescription("blacksmith_delivery");

// Get number of requirements
int reqCount = npcService.getRequirementCount("blacksmith_delivery");
// Returns -1 if quest doesn't exist
```

### Quest Chains

NPC quests can be organized into chains (sequential quest lines):

```java
// List all chain IDs
List<String> chainIds = npcService.getChainIds();

// Get quests in a chain (ordered)
List<String> questsInChain = npcService.getChainQuestIds("blacksmith_storyline");
// Returns empty list if chain doesn't exist
```

### NPC Quest Browser Example

```java
public void listNpcQuests() {
    NpcQuestService npcService = TaleQuestsAPI.get().getNpcQuestService();

    System.out.println("=== NPC Quests ===");
    for (String questId : npcService.getNpcQuestIds()) {
        String name = npcService.getQuestName(questId);
        int reqs = npcService.getRequirementCount(questId);
        System.out.println("  " + name + " (" + reqs + " objectives)");
    }

    System.out.println("\n=== Quest Chains ===");
    for (String chainId : npcService.getChainIds()) {
        List<String> quests = npcService.getChainQuestIds(chainId);
        System.out.println("  " + chainId + ":");
        for (int i = 0; i < quests.size(); i++) {
            String name = npcService.getQuestName(quests.get(i));
            System.out.println("    " + (i + 1) + ". " + name);
        }
    }
}
```

## AdminQuestService

The `AdminQuestService` provides administrative operations for managing player quest data. These operations bypass the normal quest flow: no events are fired, no rewards are given, and no items are consumed.

```java
import org.aselstudios.talequests.api.player.AdminQuestService;

TaleQuestsProvider api = TaleQuestsAPI.get();
AdminQuestService admin = api.getAdminQuestService();
```

**Important:** All admin methods return `boolean`. Returns `true` on success, `false` if the player is offline or the operation failed.

### Regular Quest Operations

```java
// Force-complete a quest (no rewards given, no events fired)
boolean success = admin.forceComplete(playerId, "myplugin:iron_miner");

// Reset a quest (removes from completed list and clears progress)
boolean success = admin.resetQuest(playerId, "myplugin:iron_miner");

// Set progress on a specific requirement
// Parameters: playerId, questId, requirementIndex, progressValue
boolean success = admin.setProgress(playerId, "myplugin:iron_miner", 0, 25);
// Sets the first requirement's progress to 25

// Reset ALL regular quests for a player (does not touch NPC or pool quests)
boolean success = admin.resetAllQuests(playerId);
```

### NPC Quest Operations

```java
// Force-complete an NPC quest
boolean success = admin.forceCompleteNpc(playerId, "blacksmith_delivery");

// Reset an NPC quest
boolean success = admin.resetNpcQuest(playerId, "blacksmith_delivery");

// Reset all NPC quests for a player
boolean success = admin.resetAllNpcQuests(playerId);
```

### Admin Command Example

```java
/**
 * Example: Give a player a head start by setting their quest progress.
 */
public void boostPlayer(UUID playerId, String questId) {
    AdminQuestService admin = TaleQuestsAPI.get().getAdminQuestService();

    // Set each requirement to 50% progress
    Quest quest = TaleQuestsAPI.get().getQuestRegistry().getQuest(questId);
    if (quest == null) return;

    List<Requirement> reqs = quest.getRequirements();
    for (int i = 0; i < reqs.size(); i++) {
        int halfAmount = reqs.get(i).getAmount() / 2;
        admin.setProgress(playerId, questId, i, halfAmount);
    }
}

/**
 * Example: Reset everything for a player (fresh start).
 */
public void fullReset(UUID playerId) {
    AdminQuestService admin = TaleQuestsAPI.get().getAdminQuestService();

    boolean questsReset = admin.resetAllQuests(playerId);
    boolean npcReset = admin.resetAllNpcQuests(playerId);

    System.out.println("Quests reset: " + questsReset + ", NPC reset: " + npcReset);
}
```

### When to Use Admin Operations

| Scenario | Method |
|----------|--------|
| Player completed a quest through external means | `forceComplete()` |
| Allow a player to redo a quest | `resetQuest()` |
| Give a player partial progress | `setProgress()` |
| Season/wipe reset for all data | `resetAllQuests()` + `resetAllNpcQuests()` |
| Debug or test quest flow | Any of the above |

### Differences from Normal Quest Flow

| Normal Flow | Admin Operations |
|-------------|------------------|
| Events fire (start, progress, complete, reward) | No events fired |
| Rewards delivered (money, items, commands) | No rewards given |
| Items removed if `removeItems` is true | No items removed |
| Prerequisites checked | Prerequisites bypassed |
| UI notifications shown | No notifications |
