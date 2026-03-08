# Quest Registration

This page covers how to create and register quests and categories through the TaleQuests API.

## Building a Quest

Use `Quest.builder(id)` to create quests with a fluent builder pattern:

```java
import org.aselstudios.talequests.api.quest.Quest;
import org.aselstudios.talequests.api.quest.RequirementType;
import org.aselstudios.talequests.api.quest.RewardType;

Quest quest = Quest.builder("myplugin:stone_collector")
    .name("Stone Collector")
    .description("Break 50 stone blocks.")
    .iconId("Rock_Stone")
    .category("gathering")
    .weight(100)
    .requirement(RequirementType.BREAK_BLOCK, "Rock_Stone", 50)
    .reward(RewardType.MONEY, "200", "$200")
    .build();
```

### Builder Methods

| Method | Required | Description |
|--------|----------|-------------|
| `name(String)` | Yes | Display name shown in the quest menu |
| `description(String)` | No | Quest description text |
| `iconId(String)` | No | Item ID used as the quest icon (e.g. `"Rock_Stone"`) |
| `displayIconTexture(String)` | No | Creature/texture name for an image icon (e.g. `"Catfish"`) |
| `category(String)` | No | Category ID this quest belongs to |
| `weight(double)` | No | Sorting weight within the category (lower = first) |
| `removeItems(boolean)` | No | Whether to take required items from inventory on completion |
| `requirement(...)` | No | Add a quest requirement (see below) |
| `prerequisite(...)` | No | Add a prerequisite (see below) |
| `reward(...)` | No | Add a reward (see below) |

### Quest ID Convention

Always prefix your quest IDs with your plugin name to prevent collisions:

```java
Quest.builder("fishing:first_catch")    // Good
Quest.builder("first_catch")            // May collide with YAML quests
```

## Requirements

Requirements define what the player needs to accomplish. A quest can have multiple requirements, and all must be completed.

### Built-in Requirement Types

| Type | Description | Example Target |
|------|-------------|----------------|
| `BREAK_BLOCK` | Break/mine blocks | `"Rock_Stone"`, `"Ore_Iron"` |
| `PLACE_BLOCK` | Place blocks | `"Soil_Dirt"`, `"Wood_Oak_Plank"` |
| `USE_BLOCK` | Interact with blocks | `"Bench_WorkBench"` |
| `KILL_MOB` | Kill creatures | `"Trork"`, `"Sheep"` |
| `KILL_PLAYER` | Defeat players | `""` (any player) |
| `HAVE_ITEM` | Have items in inventory | `"Ingredient_Bar_Iron"` |
| `CRAFT_ITEM` | Craft items | `"Tool_Pickaxe_Copper"` |
| `CHAT_MESSAGE` | Send chat messages | `""` (any message), `"hello"` |

### Adding Requirements

```java
// Standard requirement: break exactly "Rock_Stone" blocks
Quest.builder("myplugin:miner")
    .requirement(RequirementType.BREAK_BLOCK, "Rock_Stone", 50)
    .build();

// Exact match is the default. The target must match exactly.
// "Rock_Stone" matches only "Rock_Stone", not "Rock_Stone_Mossy".
```

### Wildcard Matching

Use `"*"` as the target to match anything:

```java
// Break any 100 blocks
Quest.builder("myplugin:destroyer")
    .requirement(RequirementType.BREAK_BLOCK, "*", 100)
    .build();

// Kill any mob
Quest.builder("myplugin:warrior")
    .requirement(RequirementType.KILL_MOB, "*", 20)
    .build();

// Send any chat message
Quest.builder("myplugin:talker")
    .requirement(RequirementType.CHAT_MESSAGE, "*", 10)
    .build();
```

### Contains Matching

Set `exactMatch` to `false` to match targets that contain the given text:

```java
// Break any block containing "trunk" in its ID (Oak_Trunk, Birch_Trunk, etc.)
Quest.builder("myplugin:lumberjack")
    .requirement(RequirementType.BREAK_BLOCK, "trunk", 50, false)
    .build();

// Kill any mob containing "Trork" (Trork, TrorkWarrior, TrorkArcher...)
Quest.builder("myplugin:trork_slayer")
    .requirement(RequirementType.KILL_MOB, "Trork", 15, false)
    .build();

// Match chat messages containing "hello" (case-insensitive)
Quest.builder("myplugin:greeter")
    .requirement(RequirementType.CHAT_MESSAGE, "hello", 5, false)
    .build();
```

### Custom Requirement Types

You can define your own requirement types. These are tracked via `reportProgress()`:

```java
// Custom "FISHING" requirement type
Quest.builder("fishing:first_catch")
    .requirement("FISHING", "any", 1)
    .build();

// Later, when a player catches a fish in your plugin:
api.reportProgress(playerId, "FISHING", "any", 1);
```

See [Custom Rewards and Progress](custom-rewards-and-progress.md) for details on progress reporting.

### Multiple Requirements

A quest can have multiple requirements. All must be completed:

```java
Quest.builder("myplugin:survivalist")
    .name("Survivalist")
    .description("Gather materials and craft tools.")
    .requirement(RequirementType.BREAK_BLOCK, "Rock_Stone", 20)
    .requirement(RequirementType.BREAK_BLOCK, "Wood_Oak_Trunk", 10)
    .requirement(RequirementType.CRAFT_ITEM, "Tool_Pickaxe_Copper", 1)
    .build();
```

Each requirement is tracked independently. The player sees progress for each one.

## Prerequisites

Prerequisites are conditions that must be met before a player can start a quest. There are four built-in types.

### Quest Completion Prerequisite

Requires another quest to be completed first:

```java
Quest.builder("myplugin:advanced_mining")
    .prerequisite("myplugin:basic_mining")
    .build();

// Multiple quest prerequisites
Quest.builder("myplugin:master_quest")
    .prerequisite("myplugin:quest_a")
    .prerequisite("myplugin:quest_b")
    .build();
```

### Skill Level Prerequisite

Requires a specific skill level from MmoSkillTree (optional dependency):

```java
Quest.builder("myplugin:expert_fisher")
    .prerequisiteSkillLevel("FISHING", 10)
    .build();
```

If MmoSkillTree is not installed, skill level prerequisites are automatically treated as met.

### Total Level Prerequisite

Requires a combined total level across all skills:

```java
Quest.builder("myplugin:endgame_quest")
    .prerequisiteTotalLevel(50)
    .build();
```

### Permission Prerequisite

Requires a server permission node:

```java
Quest.builder("myplugin:vip_quest")
    .prerequisitePermission("myplugin.vip")
    .build();
```

### Combining Prerequisites

You can combine any number and type of prerequisites:

```java
Quest.builder("myplugin:ultimate_challenge")
    .prerequisite("myplugin:quest_a")
    .prerequisite("myplugin:quest_b")
    .prerequisiteSkillLevel("COMBAT", 20)
    .prerequisiteTotalLevel(100)
    .prerequisitePermission("myplugin.elite")
    .build();
```

All prerequisites must be met. If any is not met, the quest shows as "LOCKED" in the menu.

### Typed Prerequisite Object

For advanced usage, you can create `Prerequisite` objects directly:

```java
import org.aselstudios.talequests.api.quest.Prerequisite;
import org.aselstudios.talequests.api.quest.PrerequisiteType;

Prerequisite prereq = new Prerequisite(
    PrerequisiteType.SKILL_LEVEL,   // type
    "FISHING",                       // target (skill name)
    10,                              // value (required level)
    "Fishing Lv.10"                  // display name (shown in UI)
);

Quest.builder("myplugin:quest")
    .prerequisite(prereq)
    .build();
```

## Rewards

Rewards are given to the player when a quest is completed.

### Built-in Reward Types

| Type | Value Format | Description |
|------|-------------|-------------|
| `MONEY` | `"500"` | Currency amount deposited to player's balance |
| `ITEM` | `"Tool_Pickaxe_Iron:1"` | Item ID with optional quantity (default 1) |
| `COMMAND` | `"say {player} won!"` | Console command. `{player}` is replaced with player name |

### Adding Rewards

The basic `reward()` method takes type, value, and display name:

```java
Quest.builder("myplugin:quest")
    // Money reward: type, value, display name
    .reward(RewardType.MONEY, "500", "$500")

    // Item reward: type, "itemId:amount", display name
    .reward(RewardType.ITEM, "Tool_Pickaxe_Iron:1", "Iron Pickaxe")
    .reward(RewardType.ITEM, "Furniture_Crude_Torch:5", "5x Torches")

    // Command reward: type, command string, display name
    .reward(RewardType.COMMAND, "say {player} completed a quest!", "Server Announcement")
    .build();
```

### Rewards with Custom Display Icons

The 4-parameter `reward()` method lets you set a display icon for the reward:

```java
Quest.builder("myplugin:quest")
    // Item icon: pass an item ID as the 4th parameter
    .reward(RewardType.COMMAND, "say {player} is a champion!", "Champion Title", "Trophy_Gold")

    // Texture icon: prefix with "texture:" for creature/NPC images
    .reward(RewardType.MONEY, "1000", "Grand Prize", "texture:Catfish")
    .build();
```

### Custom Reward Types

You can define entirely new reward types using `CustomRewardHandler`:

```java
Quest.builder("myplugin:quest")
    .reward("BROADCAST", "A player finished the quest!", "Server Broadcast")
    .build();
```

See [Custom Rewards and Progress](custom-rewards-and-progress.md) for how to implement the handler.

## Categories

Categories organize quests into groups in the quest menu.

### Registering a Category

```java
import org.aselstudios.talequests.api.category.Category;

Category category = Category.builder("fishing")
    .name("Fishing")
    .description("Quests related to fishing")
    .iconId("Tool_FishingRod_Wood")
    .weight(10)
    .build();

api.getCategoryRegistry().register(category);
```

### Builder Methods

| Method | Required | Description |
|--------|----------|-------------|
| `name(String)` | Yes | Display name shown in the menu |
| `description(String)` | No | Category description |
| `iconId(String)` | No | Item ID used as the category icon |
| `weight(double)` | No | Sorting order (lower = first) |
| `permission(String)` | No | Permission required to see this category |

### Using Existing Categories

You can assign quests to categories defined in TaleQuests' `config.yml`:

```java
// These categories are defined in the default config
Quest.builder("myplugin:quest")
    .category("gathering")    // "Gathering & Resources"
    .category("combat")       // "Combat & Hunting"
    .category("crafting")     // "Crafting & Building"
    .category("starter")      // "Starter Quests"
    .build();
```

### Querying Categories

```java
// Check if a category exists
boolean exists = api.getCategoryRegistry().isRegistered("fishing");

// Get a specific category
Category cat = api.getCategoryRegistry().getCategory("fishing");
if (cat != null) {
    String name = cat.getName();
    String desc = cat.getDescription();
}

// Get all API-registered categories
List<Category> all = api.getCategoryRegistry().getAll();
```

### Unregistering Categories

```java
api.getCategoryRegistry().unregister("fishing");
```

## Registering and Unregistering Quests

### Register

```java
api.getQuestRegistry().register(quest);
```

If a quest with the same ID already exists, the registration will fail silently.

### Unregister

```java
// Remove quest and purge all player progress data
api.getQuestRegistry().unregister("myplugin:quest_id", true);

// Remove quest but keep player progress data
api.getQuestRegistry().unregister("myplugin:quest_id", false);
```

Unregistering fires a `QuestUnregisteredEvent`.

### Querying Quests

```java
// Check if a quest exists
boolean exists = api.getQuestRegistry().isRegistered("myplugin:quest_id");

// Get a specific quest
Quest quest = api.getQuestRegistry().getQuest("myplugin:quest_id");
if (quest != null) {
    String name = quest.getName();
    List<Requirement> reqs = quest.getRequirements();
    List<Reward> rewards = quest.getRewards();
}

// Get all registered API quests
List<Quest> all = api.getQuestRegistry().getAll();

// Get quests by category
List<Quest> gatheringQuests = api.getQuestRegistry().getByCategory("gathering");

// Get quests by requirement type
List<Quest> killQuests = api.getQuestRegistry().getByRequirementType("KILL_MOB");
```

## Complete Example

```java
public void registerQuests(TaleQuestsProvider api) {
    // Register a custom category
    api.getCategoryRegistry().register(
        Category.builder("exploration")
            .name("Exploration")
            .description("Discover the world")
            .iconId("Tool_Compass")
            .weight(5)
            .build()
    );

    // Register a basic quest
    api.getQuestRegistry().register(
        Quest.builder("myplugin:forest_explorer")
            .name("Forest Explorer")
            .description("Chop down 25 trees to clear a path.")
            .iconId("Wood_Oak_Trunk")
            .category("exploration")
            .weight(1)
            .requirement(RequirementType.BREAK_BLOCK, "trunk", 25, false)
            .reward(RewardType.MONEY, "300", "$300")
            .reward(RewardType.ITEM, "Tool_Axe_Iron:1", "Iron Axe")
            .build()
    );

    // Register an advanced quest with prerequisites
    api.getQuestRegistry().register(
        Quest.builder("myplugin:mountain_climber")
            .name("Mountain Climber")
            .description("Mine 50 stone blocks at high altitude.")
            .iconId("Rock_Stone")
            .category("exploration")
            .weight(2)
            .prerequisite("myplugin:forest_explorer")
            .requirement(RequirementType.BREAK_BLOCK, "Rock_Stone", 50)
            .reward(RewardType.MONEY, "750", "$750")
            .reward(RewardType.COMMAND, "say {player} has conquered the mountain!", "Explorer Title")
            .build()
    );
}

public void unregisterQuests(TaleQuestsProvider api) {
    api.getQuestRegistry().unregister("myplugin:forest_explorer", true);
    api.getQuestRegistry().unregister("myplugin:mountain_climber", true);
    api.getCategoryRegistry().unregister("exploration");
}
```
