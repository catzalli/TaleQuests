# Custom Rewards and Progress Reporting

TaleQuests supports custom reward types and custom requirement types. This allows you to extend the quest system with game mechanics that go beyond the built-in types.

## Custom Reward Handlers

A `CustomRewardHandler` defines how a custom reward type is delivered when a player completes a quest. Custom rewards work with both API quests and YAML quests.

### Implementing a Handler

```java
import org.aselstudios.talequests.api.extension.CustomRewardHandler;

// The handler receives: playerId, value string, display name
CustomRewardHandler broadcastHandler = (playerId, value, displayName) -> {
    // value = the reward's value string from the quest definition
    // displayName = the display name shown in the UI
    CommandManager.get().handleCommand(ConsoleSender.INSTANCE, "say " + value);
};
```

### Registering and Unregistering

```java
TaleQuestsProvider api = TaleQuestsAPI.get();

// Register the handler with a type name (uppercase recommended)
api.registerRewardHandler("BROADCAST", broadcastHandler);

// Unregister when your plugin shuts down
api.unregisterRewardHandler("BROADCAST");
```

### Using Custom Rewards in API Quests

Reference the handler type name when building quest rewards:

```java
Quest.builder("myplugin:champion")
    .name("Champion")
    .description("Complete the ultimate challenge.")
    .requirement(RequirementType.KILL_MOB, "Trork", 50)
    // Custom reward: type "BROADCAST", value, display name
    .reward("BROADCAST", "A player has become a Champion!", "Server Announcement")
    // You can mix built-in and custom rewards
    .reward(RewardType.MONEY, "1000", "$1000")
    .build();
```

### Using Custom Rewards in YAML Quests

Server admins can use your custom reward type in `config.yml`:

```yaml
quests:
  champion:
    name: "Champion"
    description: "Complete the ultimate challenge."
    requirements:
      - type: KILL_MOB
        target: "Trork"
        amount: 50
    rewards:
      - type: CUSTOM
        custom_reward_type: "BROADCAST"
        value: "A player has become a Champion!"
        display_name: "Server Announcement"
```

### Handler Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `playerId` | `UUID` | The player receiving the reward (guaranteed online) |
| `value` | `String` | The reward's value string (you define the format) |
| `displayName` | `String` | The display name configured for this reward |

### Practical Examples

#### Experience Points Handler

```java
api.registerRewardHandler("EXPERIENCE", (playerId, value, displayName) -> {
    int xp = Integer.parseInt(value);
    // Call your XP system to award experience
    ExperienceManager.addXP(playerId, xp);
});

// Usage in quest:
Quest.builder("myplugin:quest")
    .reward("EXPERIENCE", "500", "500 XP")
    .build();
```

#### Permission Grant Handler

```java
api.registerRewardHandler("PERMISSION", (playerId, value, displayName) -> {
    // Grant a permission node to the player
    PermissionManager.addPermission(playerId, value);
});

// Usage in quest:
Quest.builder("myplugin:quest")
    .reward("PERMISSION", "myplugin.rank.veteran", "Veteran Rank")
    .build();
```

#### Title Handler

```java
api.registerRewardHandler("TITLE", (playerId, value, displayName) -> {
    // Set a custom title for the player
    TitleManager.setTitle(playerId, value);
});

// Usage in quest:
Quest.builder("myplugin:quest")
    .reward("TITLE", "Dragon Slayer", "Unlocks: Dragon Slayer Title")
    .build();
```

## Progress Reporting

Custom requirement types let you track progress for game mechanics that TaleQuests doesn't monitor by default. For example: fishing, trading, riding creatures, or any custom action.

### How It Works

1. Define a quest with a custom requirement type
2. When the action happens in your plugin, call `reportProgress()`
3. TaleQuests updates all matching quests automatically

### Defining Quests with Custom Requirements

```java
// A quest that requires catching fish
Quest.builder("fishing:first_catch")
    .name("First Catch")
    .description("Catch your first fish.")
    .requirement("FISHING", "any", 1)    // Custom type "FISHING"
    .build();

// A quest that requires catching a specific fish
Quest.builder("fishing:golden_fish")
    .name("Golden Catch")
    .description("Catch 3 Golden Fish.")
    .requirement("FISHING", "GoldenFish", 3)
    .build();
```

### Reporting Progress

```java
import org.aselstudios.talequests.api.ProgressResult;

TaleQuestsProvider api = TaleQuestsAPI.get();

// When a player catches a fish in your plugin:
ProgressResult result = api.reportProgress(
    playerId,       // Player UUID
    "FISHING",      // Requirement type (must match quest definition)
    "GoldenFish",   // Target (matched against quest requirement targets)
    1               // Amount of progress to add
);
```

### Progress Report Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `playerId` | `UUID` | The player who performed the action |
| `requirementType` | `String` | The custom requirement type name |
| `target` | `String` | What the player interacted with |
| `amount` | `int` | How much progress to add |

### ProgressResult

The return value tells you what happened:

| Result | Description |
|--------|-------------|
| `OK` | Progress was applied to at least one quest |
| `PLAYER_OFFLINE` | The player is not online or data not loaded |
| `NO_MATCHING_QUESTS` | No active quests match this type and target |
| `API_NOT_READY` | TaleQuests API is not initialized |
| `INVALID_INPUT` | Bad parameters (null values, empty strings) |

### Handling the Result

```java
ProgressResult result = api.reportProgress(playerId, "FISHING", "GoldenFish", 1);

switch (result) {
    case OK -> {
        // Progress applied successfully
    }
    case PLAYER_OFFLINE -> {
        // Player is not online, skip
    }
    case NO_MATCHING_QUESTS -> {
        // No quests use this requirement. This is normal if the player
        // has no active FISHING quests or has already completed them.
    }
    case API_NOT_READY -> {
        // TaleQuests is not loaded. Should not happen if you checked
        // TaleQuestsAPI.isAvailable() at startup.
    }
    case INVALID_INPUT -> {
        // Check your parameters
    }
}
```

### Target Matching

Progress is matched against quest requirements using the same rules as built-in types:

* **Exact match** (default): `"GoldenFish"` matches only `"GoldenFish"`
* **Wildcard target**: A quest with target `"*"` matches any progress report
* **Contains match**: A quest with `exactMatch=false` and target `"fish"` matches `"GoldenFish"`, `"Swordfish"`, etc.

```java
// This progress report...
api.reportProgress(playerId, "FISHING", "GoldenFish", 1);

// ...will match these quest requirements:
// requirement("FISHING", "GoldenFish", 3)        -> exact match
// requirement("FISHING", "*", 10)                 -> wildcard match
// requirement("FISHING", "fish", 5, false)        -> contains match
```

## Complete Example: Fishing Plugin

```java
public class FishingPlugin extends JavaPlugin {

    public FishingPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();

        if (!TaleQuestsAPI.isAvailable()) {
            getLogger().at(Level.WARNING).log("TaleQuests not found. Quests disabled.");
            return;
        }

        TaleQuestsProvider api = TaleQuestsAPI.get();

        // Register a "fishing" category
        api.getCategoryRegistry().register(
            Category.builder("fishing")
                .name("Fishing")
                .description("Quests for anglers")
                .iconId("Tool_FishingRod_Wood")
                .weight(15)
                .build()
        );

        // Register fishing quests
        api.getQuestRegistry().register(
            Quest.builder("fishing:beginner")
                .name("Beginner Angler")
                .description("Catch 5 fish of any type.")
                .iconId("Tool_FishingRod_Wood")
                .category("fishing")
                .weight(1)
                .requirement("FISHING", "*", 5)
                .reward(RewardType.MONEY, "100", "$100")
                .build()
        );

        api.getQuestRegistry().register(
            Quest.builder("fishing:rare_catch")
                .name("Rare Catch")
                .description("Catch 3 Golden Fish.")
                .iconId("Tool_FishingRod_Iron")
                .category("fishing")
                .weight(2)
                .prerequisite("fishing:beginner")
                .requirement("FISHING", "GoldenFish", 3)
                .reward(RewardType.MONEY, "500", "$500")
                .reward(RewardType.ITEM, "Trophy_Gold:1", "Gold Trophy")
                .build()
        );

        // Register a custom reward for fishing achievements
        api.registerRewardHandler("FISHING_TITLE", (playerId, value, displayName) -> {
            CommandManager.get().handleCommand(
                ConsoleSender.INSTANCE,
                "say A player earned the title: " + value
            );
        });

        api.getQuestRegistry().register(
            Quest.builder("fishing:master")
                .name("Master Angler")
                .description("Catch 100 fish of any type.")
                .iconId("Tool_FishingRod_Iron")
                .category("fishing")
                .weight(3)
                .prerequisite("fishing:rare_catch")
                .requirement("FISHING", "*", 100)
                .reward(RewardType.MONEY, "2000", "$2000")
                .reward("FISHING_TITLE", "Master Angler", "Unlocks: Master Angler Title")
                .build()
        );

        getLogger().at(Level.INFO).log("Fishing quests registered!");
    }

    /**
     * Call this method when a player catches a fish in your fishing system.
     */
    public void onFishCaught(UUID playerId, String fishType) {
        if (!TaleQuestsAPI.isAvailable()) return;

        ProgressResult result = TaleQuestsAPI.get().reportProgress(
            playerId, "FISHING", fishType, 1
        );

        if (result == ProgressResult.OK) {
            // Progress was applied to at least one quest
        }
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        if (!TaleQuestsAPI.isAvailable()) return;

        TaleQuestsProvider api = TaleQuestsAPI.get();
        api.getQuestRegistry().unregister("fishing:beginner", true);
        api.getQuestRegistry().unregister("fishing:rare_catch", true);
        api.getQuestRegistry().unregister("fishing:master", true);
        api.unregisterRewardHandler("FISHING_TITLE");
        api.getCategoryRegistry().unregister("fishing");
    }
}
```
