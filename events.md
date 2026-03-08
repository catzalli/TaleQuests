# Events

TaleQuests provides an event system that lets you react to quest lifecycle changes. Events fire for both API-registered quests and YAML-defined quests.

## Subscribing to Events

Use the `EventBus` to subscribe handlers:

```java
import org.aselstudios.talequests.api.event.EventHandler;
import org.aselstudios.talequests.api.event.QuestCompleteEvent;

TaleQuestsProvider api = TaleQuestsAPI.get();

// Subscribe with a lambda
api.getEventBus().subscribe(QuestCompleteEvent.class, event -> {
    System.out.println(event.getPlayerId() + " completed " + event.getQuestName());
});
```

### Keeping Handler References

To unsubscribe later, store a reference to your handler:

```java
EventHandler<QuestCompleteEvent> handler = event -> {
    System.out.println(event.getPlayerId() + " completed " + event.getQuestName());
};

// Subscribe
api.getEventBus().subscribe(QuestCompleteEvent.class, handler);

// Unsubscribe when no longer needed
api.getEventBus().unsubscribe(QuestCompleteEvent.class, handler);
```

### Clearing All Handlers

Remove all subscriptions at once (useful for plugin shutdown):

```java
api.getEventBus().unsubscribeAll();
```

## Event Types

### QuestStartEvent

Fires when a player first begins working on a quest (first progress increment).

**Not cancellable.** This is a notification event.

```java
api.getEventBus().subscribe(QuestStartEvent.class, event -> {
    UUID playerId  = event.getPlayerId();
    String questId = event.getQuestId();
    String name    = event.getQuestName();
    boolean isApi  = event.isApiQuest();

    System.out.println(playerId + " started quest: " + name);
});
```

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayerId()` | `UUID` | The player's unique ID |
| `getQuestId()` | `String` | The quest's ID |
| `getQuestName()` | `String` | The quest's display name |
| `isApiQuest()` | `boolean` | `true` if this is an API quest, `false` for YAML |

### QuestProgressEvent

Fires before a progress update is applied. **Cancellable.** Cancel this event to prevent the progress from being recorded.

```java
api.getEventBus().subscribe(QuestProgressEvent.class, event -> {
    UUID playerId       = event.getPlayerId();
    String questId      = event.getQuestId();
    String reqType      = event.getRequirementType();
    String target       = event.getTarget();
    int reqIndex        = event.getRequirementIndex();
    int oldProgress     = event.getOldProgress();
    int newProgress     = event.getNewProgress();
    int required        = event.getRequired();
    boolean isApi       = event.isApiQuest();

    System.out.println(
        questId + " progress: " + oldProgress + " -> " + newProgress + "/" + required
    );

    // Example: block progress after a certain time
    // event.setCancelled(true);
});
```

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayerId()` | `UUID` | The player's unique ID |
| `getQuestId()` | `String` | The quest's ID |
| `getRequirementType()` | `String` | Requirement type (e.g. `"BREAK_BLOCK"`) |
| `getTarget()` | `String` | Requirement target (e.g. `"Rock_Stone"`) |
| `getRequirementIndex()` | `int` | Index of the requirement (0-based) |
| `getOldProgress()` | `int` | Progress before this update |
| `getNewProgress()` | `int` | Progress after this update |
| `getRequired()` | `int` | Total amount required |
| `isApiQuest()` | `boolean` | Whether this is an API quest |
| `isCancelled()` | `boolean` | Whether the event is cancelled |
| `setCancelled(boolean)` | `void` | Cancel or uncancel the event |

### QuestCompleteEvent

Fires after a quest is marked as completed. **Not cancellable.** This is a notification event.

```java
api.getEventBus().subscribe(QuestCompleteEvent.class, event -> {
    UUID playerId  = event.getPlayerId();
    String questId = event.getQuestId();
    String name    = event.getQuestName();
    boolean isApi  = event.isApiQuest();

    System.out.println(playerId + " completed quest: " + name);
});
```

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayerId()` | `UUID` | The player's unique ID |
| `getQuestId()` | `String` | The quest's ID |
| `getQuestName()` | `String` | The quest's display name |
| `isApiQuest()` | `boolean` | Whether this is an API quest |

### QuestRewardEvent

Fires before rewards are delivered to the player. **Cancellable.** Cancel this event to prevent all rewards from being given.

```java
import org.aselstudios.talequests.api.quest.Reward;

api.getEventBus().subscribe(QuestRewardEvent.class, event -> {
    UUID playerId  = event.getPlayerId();
    String questId = event.getQuestId();
    boolean isApi  = event.isApiQuest();

    // Get the list of rewards (unmodifiable)
    List<Reward> rewards = event.getRewards();
    for (Reward reward : rewards) {
        System.out.println("Reward: " + reward.getType() + " = " + reward.getValue());
    }

    // Example: prevent rewards for a specific quest
    // if ("some_quest_id".equals(questId)) {
    //     event.setCancelled(true);
    // }
});
```

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayerId()` | `UUID` | The player's unique ID |
| `getQuestId()` | `String` | The quest's ID |
| `getRewards()` | `List<Reward>` | Unmodifiable list of rewards |
| `isApiQuest()` | `boolean` | Whether this is an API quest |
| `isCancelled()` | `boolean` | Whether the event is cancelled |
| `setCancelled(boolean)` | `void` | Cancel or uncancel the event |

### QuestUnregisteredEvent

Fires when an API quest is unregistered via `QuestRegistry.unregister()`. **Not cancellable.**

```java
api.getEventBus().subscribe(QuestUnregisteredEvent.class, event -> {
    String questId     = event.getQuestId();
    boolean purged     = event.isProgressPurged();

    System.out.println("Quest unregistered: " + questId + " (purged=" + purged + ")");
});
```

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getQuestId()` | `String` | The quest that was unregistered |
| `isProgressPurged()` | `boolean` | Whether player progress data was removed |

## Cancellable Events

Two events can be cancelled: `QuestProgressEvent` and `QuestRewardEvent`.

When an event is cancelled:

* **QuestProgressEvent:** The progress increment is not applied. The player's progress stays at the old value.
* **QuestRewardEvent:** No rewards are given (money, items, commands, custom rewards are all skipped). The quest is still marked as completed.

```java
// Block progress on a specific quest during an event
api.getEventBus().subscribe(QuestProgressEvent.class, event -> {
    if ("myplugin:special_quest".equals(event.getQuestId())) {
        // Only allow progress between 6 PM and midnight
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 18) {
            event.setCancelled(true);
        }
    }
});

// Double-check rewards before delivery
api.getEventBus().subscribe(QuestRewardEvent.class, event -> {
    if (event.isApiQuest() && event.getQuestId().startsWith("myplugin:")) {
        // Custom validation logic
        boolean eligible = checkEligibility(event.getPlayerId());
        if (!eligible) {
            event.setCancelled(true);
        }
    }
});
```

## Identifying API vs YAML Quests

Every event includes `isApiQuest()` which returns `true` for quests registered through the API, and `false` for quests defined in `config.yml`.

```java
api.getEventBus().subscribe(QuestCompleteEvent.class, event -> {
    if (event.isApiQuest()) {
        // This is a quest registered by a plugin
    } else {
        // This is a quest from config.yml
    }
});
```

## Complete Example

```java
public class EventListenerSetup {

    private final TaleQuestsProvider api;
    private final HytaleLogger logger;

    private final EventHandler<QuestStartEvent> startHandler;
    private final EventHandler<QuestCompleteEvent> completeHandler;
    private final EventHandler<QuestProgressEvent> progressHandler;
    private final EventHandler<QuestRewardEvent> rewardHandler;

    public EventListenerSetup(TaleQuestsProvider api, HytaleLogger logger) {
        this.api = api;
        this.logger = logger;

        startHandler = event -> {
            logger.at(Level.INFO).log(
                event.getPlayerId() + " started: " + event.getQuestName()
            );
        };

        completeHandler = event -> {
            logger.at(Level.INFO).log(
                event.getPlayerId() + " completed: " + event.getQuestName()
            );
        };

        progressHandler = event -> {
            logger.at(Level.INFO).log(
                event.getQuestId() + ": " +
                event.getOldProgress() + " -> " + event.getNewProgress() +
                "/" + event.getRequired()
            );
        };

        rewardHandler = event -> {
            logger.at(Level.INFO).log(
                event.getPlayerId() + " receiving " +
                event.getRewards().size() + " rewards for " + event.getQuestId()
            );
        };
    }

    public void register() {
        api.getEventBus().subscribe(QuestStartEvent.class, startHandler);
        api.getEventBus().subscribe(QuestCompleteEvent.class, completeHandler);
        api.getEventBus().subscribe(QuestProgressEvent.class, progressHandler);
        api.getEventBus().subscribe(QuestRewardEvent.class, rewardHandler);
    }

    public void unregister() {
        api.getEventBus().unsubscribe(QuestStartEvent.class, startHandler);
        api.getEventBus().unsubscribe(QuestCompleteEvent.class, completeHandler);
        api.getEventBus().unsubscribe(QuestProgressEvent.class, progressHandler);
        api.getEventBus().unsubscribe(QuestRewardEvent.class, rewardHandler);
    }
}
```
