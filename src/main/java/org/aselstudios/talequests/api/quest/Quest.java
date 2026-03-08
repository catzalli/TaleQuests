package org.aselstudios.talequests.api.quest;

import java.util.Collections;
import java.util.List;

/**
 * An immutable quest definition registered via the API.
 *
 * <p>Use {@link #builder(String)} to create instances:</p>
 * <pre>{@code
 * Quest quest = Quest.builder("myplugin:catch_fish")
 *     .name("Master Fisher")
 *     .description("Catch 10 salmon")
 *     .requirement("FISHING", "salmon", 10)
 *     .reward(RewardType.MONEY, "500", "$500")
 *     .build();
 * }</pre>
 */
public final class Quest {

    private final String id;
    private final String name;
    private final String description;
    private final String iconId;
    private final String displayIconTexture;
    private final String category;
    private final double weight;
    private final boolean removeItems;
    private final List<String> prerequisiteIds;
    private final List<Prerequisite> prerequisites;
    private final List<Requirement> requirements;
    private final List<Reward> rewards;

    Quest(QuestBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.iconId = builder.iconId;
        this.displayIconTexture = builder.displayIconTexture;
        this.category = builder.category;
        this.weight = builder.weight;
        this.removeItems = builder.removeItems;
        this.prerequisiteIds = List.copyOf(builder.prerequisiteIds);
        this.prerequisites = List.copyOf(builder.prerequisites);
        this.requirements = List.copyOf(builder.requirements);
        this.rewards = List.copyOf(builder.rewards);
    }

    /** Returns the unique quest ID (recommended format: {@code "pluginname:questid"}). */
    public String getId() { return id; }

    /** Returns the quest display name. */
    public String getName() { return name; }

    /** Returns the quest description. */
    public String getDescription() { return description; }

    /** Returns the icon ID, or {@code null} if not set. */
    public String getIconId() { return iconId; }

    /**
     * Returns the display icon texture name for creature/NPC-style icons,
     * or {@code null} if not set.
     *
     * <p>When set, the UI uses an image texture instead of an ItemSlot for
     * the quest icon. The value is a creature texture name (e.g. {@code "Bear_Grizzly"})
     * or a {@code "texture:"}-prefixed path for arbitrary textures.</p>
     */
    public String getDisplayIconTexture() { return displayIconTexture; }

    /** Returns the category ID this quest belongs to, or {@code null}. */
    public String getCategory() { return category; }

    /** Returns the sort weight (lower values appear first). */
    public double getWeight() { return weight; }

    /** Returns {@code true} if HAVE_ITEM requirements should consume items on completion. */
    public boolean isRemoveItems() { return removeItems; }

    /** Returns an unmodifiable list of prerequisite quest IDs (QUEST_COMPLETE targets only). */
    public List<String> getPrerequisiteIds() { return prerequisiteIds; }

    /** Returns an unmodifiable list of typed prerequisites. */
    public List<Prerequisite> getPrerequisites() { return prerequisites; }

    /** Returns an unmodifiable list of requirements. */
    public List<Requirement> getRequirements() { return requirements; }

    /** Returns an unmodifiable list of rewards. */
    public List<Reward> getRewards() { return rewards; }

    /**
     * Creates a new quest builder with the given ID.
     *
     * @param id the unique quest ID (recommended format: {@code "pluginname:questid"})
     */
    public static QuestBuilder builder(String id) {
        return new QuestBuilder(id);
    }

    @Override
    public String toString() {
        return "Quest{id=" + id + ", name=" + name + "}";
    }
}
