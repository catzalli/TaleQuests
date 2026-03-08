# TaleQuests API

TaleQuests is a quest system plugin for Hytale servers. The API allows other plugins to register custom quests, categories, reward handlers, listen to quest events, query player data, and perform administrative operations.

## Installation

You can easily add TaleQuestsAPI to your project using either Gradle or Maven. 

### Gradle

Add the Asel Studios repository and the TaleQuestsAPI dependency to your `build.gradle` file. Make sure to use `compileOnly` so the API is not bundled into your plugin.

```gradle
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0.0'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    maven {
        name = 'Asel Studios Repository'
        url = 'https://repo.aselstudios.com/releases'
    }
}

dependencies {
    // Hytale server API
    compileOnly(files(
        System.getProperty("user.home") +
        "/AppData/Roaming/Hytale/install/release/package/game/latest/Server/HytaleServer.jar"
    ))

    // TaleQuests API
    compileOnly 'org.aselstudios:TaleQuestsAPI:1.0.5'
}

tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
    options.release.set(25)
}
```

### Maven
```maven
<repositories>
    <repository>
        <id>aselstudios-releases</id>
        <name>Asel Studios Repository</name>
        <url>https://repo.aselstudios.com/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.aselstudios</groupId>
        <artifactId>TaleQuestsAPI</artifactId>
        <version>1.0.5</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### 3. Configure manifest.json

Add TaleQuests as a dependency so your plugin loads after TaleQuests:

```json
{
  "Group": "YourName",
  "Name": "YourPlugin",
  "Version": "1.0.0",
  "Description": "My custom quest plugin.",
  "Authors": [
    { "Name": "YourName" }
  ],
  "Dependencies": {
    "AselStudios:TaleQuests": "*"
  },
  "Main": "com.example.yourplugin.YourPlugin"
}
```

### 4. Access the API

```java
import org.aselstudios.talequests.api.TaleQuestsAPI;
import org.aselstudios.talequests.api.TaleQuestsProvider;

// Check availability first
if (!TaleQuestsAPI.isAvailable()) {
    getLogger().at(Level.SEVERE).log("TaleQuests not found!");
    return;
}

// Get the API provider
TaleQuestsProvider api = TaleQuestsAPI.get();
```

## Quick Start

Here is a minimal plugin that registers a quest and listens for its completion:

```java
package com.example.myplugin;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.aselstudios.talequests.api.TaleQuestsAPI;
import org.aselstudios.talequests.api.TaleQuestsProvider;
import org.aselstudios.talequests.api.event.QuestCompleteEvent;
import org.aselstudios.talequests.api.quest.Quest;
import org.aselstudios.talequests.api.quest.RequirementType;
import org.aselstudios.talequests.api.quest.RewardType;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.logging.Level;

public class MyPlugin extends JavaPlugin {

    public MyPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();

        if (!TaleQuestsAPI.isAvailable()) {
            getLogger().at(Level.SEVERE).log("TaleQuests not found!");
            return;
        }

        TaleQuestsProvider api = TaleQuestsAPI.get();

        // Register a quest
        api.getQuestRegistry().register(
            Quest.builder("myplugin:iron_miner")
                .name("Iron Miner")
                .description("Mine 30 iron ore blocks.")
                .iconId("Ore_Iron")
                .category("gathering")
                .requirement(RequirementType.BREAK_BLOCK, "Ore_Iron", 30)
                .reward(RewardType.MONEY, "500", "$500")
                .reward(RewardType.ITEM, "Tool_Pickaxe_Iron:1", "Iron Pickaxe")
                .build()
        );

        // Listen for quest completions
        api.getEventBus().subscribe(QuestCompleteEvent.class, event -> {
            getLogger().at(Level.INFO).log(
                event.getPlayerId() + " completed: " + event.getQuestName()
            );
        });

        getLogger().at(Level.INFO).log("MyPlugin loaded with TaleQuests integration!");
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        if (!TaleQuestsAPI.isAvailable()) return;

        TaleQuestsProvider api = TaleQuestsAPI.get();
        api.getQuestRegistry().unregister("myplugin:iron_miner", true);
        api.getEventBus().unsubscribeAll();
    }
}
```

## API Overview

| Area | Interface | Description |
|------|-----------|-------------|
| Quest Registration | `QuestRegistry` | Register and manage custom quests |
| Category Registration | `CategoryRegistry` | Register and manage quest categories |
| Event System | `EventBus` | Subscribe to quest lifecycle events |
| Custom Rewards | `CustomRewardHandler` | Define custom reward types |
| Player Data | `PlayerDataService` | Query player quest states |
| Pool Queries | `PoolService` | Read daily/weekly pool info |
| NPC Queries | `NpcQuestService` | Read NPC quest definitions |
| Admin Operations | `AdminQuestService` | Force complete, reset, set progress |
| Progress Reporting | `reportProgress()` | Report custom requirement progress |

## Documentation Pages

| Page | Contents |
|------|----------|
| [Quest Registration](quest-registration.md) | Quests, requirements, prerequisites, rewards, categories |
| [Events](events.md) | Event types, subscribing, cancellation |
| [Player Data](player-data.md) | Querying player progress, quest status |
| [Services](services.md) | Pool, NPC, and admin services |
| [Custom Rewards and Progress](custom-rewards-and-progress.md) | Custom reward handlers, progress reporting |

## Important Notes

* **Namespace your quest IDs.** Use a prefix like `myplugin:quest_name` to avoid collisions with YAML quests and other plugins.
* **Clean up on shutdown.** Unregister your quests, reward handlers, and event listeners when your plugin is disabled.
* **API quests and YAML quests coexist.** Players see both in the same quest menu. Events fire for both types.
* **Thread safety.** Player data snapshots are immutable. The API is safe to call from any thread.
* **All data objects are immutable.** `Quest`, `Category`, `Requirement`, `Reward`, `QuestPlayer`, and `PoolQuestInfo` are all read-only snapshots.





