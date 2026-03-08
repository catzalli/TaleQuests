package org.aselstudios.talequests.api.quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for creating {@link Quest} instances.
 *
 * <p>At minimum, an ID, name, and at least one requirement must be provided.</p>
 *
 * @see Quest#builder(String)
 */
public final class QuestBuilder {

    final String id;
    String name;
    String description = "";
    String iconId;
    String displayIconTexture;
    String category;
    double weight = 0.0;
    boolean removeItems = false;
    final List<String> prerequisiteIds = new ArrayList<>();
    final List<Prerequisite> prerequisites = new ArrayList<>();
    final List<Requirement> requirements = new ArrayList<>();
    final List<Reward> rewards = new ArrayList<>();

    QuestBuilder(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("quest id must not be null or blank");
        }
        this.id = id;
    }

    /** Sets the quest display name. Required. */
    public QuestBuilder name(String name) {
        this.name = name;
        return this;
    }

    /** Sets the quest description. */
    public QuestBuilder description(String description) {
        this.description = description != null ? description : "";
        return this;
    }

    /** Sets the icon ID used in the UI (shown as an ItemSlot). */
    public QuestBuilder iconId(String iconId) {
        this.iconId = iconId;
        return this;
    }

    /**
     * Sets the display icon texture for creature/NPC-style icons.
     *
     * <p>When set, the UI uses an image texture instead of an ItemSlot for
     * the quest icon. Use a creature texture name (e.g. {@code "Bear_Grizzly"})
     * or a {@code "texture:"}-prefixed path for arbitrary textures.</p>
     */
    public QuestBuilder displayIconTexture(String textureName) {
        this.displayIconTexture = textureName;
        return this;
    }

    /** Sets the category ID this quest belongs to. */
    public QuestBuilder category(String categoryId) {
        this.category = categoryId;
        return this;
    }

    /** Sets the sort weight (lower values appear first). Default is 0. */
    public QuestBuilder weight(double weight) {
        this.weight = weight;
        return this;
    }

    /**
     * If {@code true}, HAVE_ITEM requirements will consume items from
     * the player's inventory upon quest completion. Default is {@code false}.
     */
    public QuestBuilder removeItems(boolean removeItems) {
        this.removeItems = removeItems;
        return this;
    }

    /** Adds a prerequisite quest ID. The prerequisite must be completed before this quest can progress. */
    public QuestBuilder prerequisite(String questId) {
        if (questId != null && !questId.isBlank()) {
            this.prerequisiteIds.add(questId);
            this.prerequisites.add(new Prerequisite(PrerequisiteType.QUEST_COMPLETE, questId, 0));
        }
        return this;
    }

    /** Adds multiple prerequisite quest IDs. */
    public QuestBuilder prerequisites(List<String> questIds) {
        if (questIds != null) {
            for (String id : questIds) {
                prerequisite(id);
            }
        }
        return this;
    }

    /**
     * Adds a typed prerequisite.
     *
     * @param prerequisite the prerequisite to add
     */
    public QuestBuilder prerequisite(Prerequisite prerequisite) {
        if (prerequisite != null) {
            this.prerequisites.add(prerequisite);
            if (PrerequisiteType.QUEST_COMPLETE.equals(prerequisite.getType())
                    && !prerequisite.getTarget().isEmpty()) {
                this.prerequisiteIds.add(prerequisite.getTarget());
            }
        }
        return this;
    }

    /**
     * Adds a skill level prerequisite (requires MmoSkillTree).
     *
     * @param skillId  the skill ID (e.g. {@code "MINING"}, {@code "FISHING"})
     * @param minLevel the minimum skill level required
     */
    public QuestBuilder prerequisiteSkillLevel(String skillId, int minLevel) {
        this.prerequisites.add(new Prerequisite(PrerequisiteType.SKILL_LEVEL, skillId, minLevel));
        return this;
    }

    /**
     * Adds a total skill level prerequisite (requires MmoSkillTree).
     *
     * @param minLevel the minimum combined total level required
     */
    public QuestBuilder prerequisiteTotalLevel(int minLevel) {
        this.prerequisites.add(new Prerequisite(PrerequisiteType.TOTAL_LEVEL, "", minLevel));
        return this;
    }

    /**
     * Adds a permission prerequisite.
     *
     * @param permission the permission node the player must have
     */
    public QuestBuilder prerequisitePermission(String permission) {
        this.prerequisites.add(new Prerequisite(PrerequisiteType.PERMISSION, permission, 0));
        return this;
    }

    /**
     * Adds a permission prerequisite with a custom display name.
     *
     * @param permission the permission node the player must have
     * @param display    the display text shown in the GUI (e.g. "VIP Only")
     */
    public QuestBuilder prerequisitePermission(String permission, String display) {
        this.prerequisites.add(new Prerequisite(PrerequisiteType.PERMISSION, permission, 0, display));
        return this;
    }

    /**
     * Adds a requirement to this quest with exact matching (default).
     *
     * <p>Type strings are normalized to {@code UPPER_CASE}.
     * Use built-in constants from {@link RequirementType} or any custom string.</p>
     *
     * @param type   the requirement type (e.g. {@code "KILL_MOB"}, {@code "FISHING"})
     * @param target the target identifier (e.g. mob ID, item ID, or {@code "*"} for wildcard)
     * @param amount the required amount (must be positive)
     */
    public QuestBuilder requirement(String type, String target, int amount) {
        this.requirements.add(new Requirement(type, target, amount));
        return this;
    }

    /**
     * Adds a requirement to this quest with configurable matching mode.
     *
     * <p>Type strings are normalized to {@code UPPER_CASE}.
     * Use built-in constants from {@link RequirementType} or any custom string.</p>
     *
     * @param type       the requirement type (e.g. {@code "KILL_MOB"}, {@code "FISHING"})
     * @param target     the target identifier (e.g. mob ID, item ID, or {@code "*"} for wildcard)
     * @param amount     the required amount (must be positive)
     * @param exactMatch {@code true} for exact matching (default), {@code false} for contains matching
     */
    public QuestBuilder requirement(String type, String target, int amount, boolean exactMatch) {
        this.requirements.add(new Requirement(type, target, amount, exactMatch));
        return this;
    }

    /**
     * Adds a reward to this quest.
     *
     * <p>Type strings are normalized to {@code UPPER_CASE}.
     * Use built-in constants from {@link RewardType} or any custom string.
     * Register a {@link org.aselstudios.talequests.api.extension.CustomRewardHandler}
     * for custom reward types.</p>
     *
     * @param type        the reward type (e.g. {@code "MONEY"}, {@code "XP"})
     * @param value       the type-specific value
     * @param displayName the display name shown to the player
     */
    public QuestBuilder reward(String type, String value, String displayName) {
        this.rewards.add(new Reward(type, value, displayName));
        return this;
    }

    /**
     * Adds a reward to this quest with a custom display icon.
     *
     * <p>Type strings are normalized to {@code UPPER_CASE}.
     * Use built-in constants from {@link RewardType} or any custom string.
     * Register a {@link org.aselstudios.talequests.api.extension.CustomRewardHandler}
     * for custom reward types.</p>
     *
     * @param type        the reward type (e.g. {@code "MONEY"}, {@code "XP"})
     * @param value       the type-specific value
     * @param displayName the display name shown to the player
     * @param displayIcon optional icon override — an item ID or a {@code "texture:path"} string,
     *                    or {@code null} for the default icon
     */
    public QuestBuilder reward(String type, String value, String displayName, String displayIcon) {
        this.rewards.add(new Reward(type, value, displayName, displayIcon));
        return this;
    }

    /**
     * Builds and returns an immutable {@link Quest} instance.
     *
     * @throws IllegalStateException if required fields are missing
     */
    public Quest build() {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("quest name is required (id=" + id + ")");
        }
        if (requirements.isEmpty()) {
            throw new IllegalStateException("quest must have at least one requirement (id=" + id + ")");
        }
        return new Quest(this);
    }
}
