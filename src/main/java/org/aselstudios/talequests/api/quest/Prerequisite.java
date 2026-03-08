package org.aselstudios.talequests.api.quest;

import java.util.Locale;

/**
 * An immutable prerequisite condition for a quest.
 *
 * <p>Prerequisites gate quest progress — all must be satisfied before a player
 * can make progress on the quest. Built-in types include:</p>
 * <ul>
 *   <li>{@link PrerequisiteType#QUEST_COMPLETE} — another quest must be completed</li>
 *   <li>{@link PrerequisiteType#SKILL_LEVEL} — MmoSkillTree skill level (optional dependency)</li>
 *   <li>{@link PrerequisiteType#TOTAL_LEVEL} — combined skill level total</li>
 *   <li>{@link PrerequisiteType#PERMISSION} — server permission node</li>
 * </ul>
 */
public final class Prerequisite {

    private final String type;
    private final String target;
    private final int value;
    private final String display;

    /**
     * Creates a new prerequisite.
     *
     * @param type   the prerequisite type (normalized to UPPER_CASE)
     * @param target the target identifier (quest ID, skill ID, permission node, etc.)
     * @param value  the minimum value (level, amount) — 0 for types that don't need it
     */
    public Prerequisite(String type, String target, int value) {
        this(type, target, value, null);
    }

    /**
     * Creates a new prerequisite with an optional display name.
     *
     * @param type    the prerequisite type (normalized to UPPER_CASE)
     * @param target  the target identifier (quest ID, skill ID, permission node, etc.)
     * @param value   the minimum value (level, amount) — 0 for types that don't need it
     * @param display optional display text shown in the GUI (e.g. "VIP Only" for PERMISSION)
     */
    public Prerequisite(String type, String target, int value, String display) {
        if (type == null || type.isBlank()) throw new IllegalArgumentException("type must not be null or blank");
        this.type = type.toUpperCase(Locale.ROOT);
        this.target = target != null ? target : "";
        this.value = Math.max(0, value);
        this.display = display;
    }

    /** Returns the prerequisite type (e.g. {@code "QUEST_COMPLETE"}, {@code "SKILL_LEVEL"}). */
    public String getType() { return type; }

    /** Returns the target identifier (quest ID, skill ID, permission node, etc.). */
    public String getTarget() { return target; }

    /** Returns the minimum value (level, amount), or 0 for types that don't use it. */
    public int getValue() { return value; }

    /** Returns the optional display text for GUI, or {@code null} if not set. */
    public String getDisplay() { return display; }

    @Override
    public String toString() {
        return "Prerequisite{type=" + type + ", target=" + target + ", value=" + value
                + (display != null ? ", display=" + display : "") + "}";
    }
}
