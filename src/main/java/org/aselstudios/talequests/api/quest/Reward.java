package org.aselstudios.talequests.api.quest;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * An immutable quest reward definition.
 *
 * <p>Each reward specifies a type (built-in or custom), a value whose
 * meaning depends on the type, a display name for the UI, and an
 * optional display icon override.</p>
 */
public final class Reward {

    private final String type;
    private final String value;
    private final String displayName;
    private final @Nullable String displayIcon;

    /**
     * Creates a new reward without a custom display icon.
     *
     * @param type        the reward type, normalized to UPPER_CASE
     *                    (e.g. {@code "MONEY"}, {@code "COMMAND"}, {@code "ITEM"}, or a custom type)
     * @param value       the type-specific value (e.g. amount, command string, item ID)
     * @param displayName the display name shown to the player
     */
    public Reward(String type, String value, String displayName) {
        this(type, value, displayName, null);
    }

    /**
     * Creates a new reward with an optional custom display icon.
     *
     * @param type        the reward type, normalized to UPPER_CASE
     *                    (e.g. {@code "MONEY"}, {@code "COMMAND"}, {@code "ITEM"}, or a custom type)
     * @param value       the type-specific value (e.g. amount, command string, item ID)
     * @param displayName the display name shown to the player
     * @param displayIcon optional icon override — an item ID (e.g. {@code "Tool_Pickaxe_Iron"})
     *                    or a texture path prefixed with {@code "texture:"} (e.g. {@code "texture:../Pages/Memories/npcs/Bear_Grizzly.png"}).
     *                    Pass {@code null} to use the default icon for this reward type.
     */
    public Reward(String type, String value, String displayName, @Nullable String displayIcon) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be null or blank");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value must not be null or blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be null or blank");
        }
        this.type = type.toUpperCase(Locale.ROOT);
        this.value = value;
        this.displayName = displayName;
        this.displayIcon = displayIcon;
    }

    /** Returns the reward type, always in UPPER_CASE. */
    public String getType() { return type; }

    /** Returns the type-specific value. */
    public String getValue() { return value; }

    /** Returns the display name shown to the player. */
    public String getDisplayName() { return displayName; }

    /**
     * Returns the custom display icon, or {@code null} if the default icon should be used.
     *
     * <p>The value is either an item ID (e.g. {@code "Tool_Pickaxe_Iron"}) or a texture
     * path prefixed with {@code "texture:"} (e.g. {@code "texture:../Pages/Memories/npcs/Bear_Grizzly.png"}).</p>
     */
    public @Nullable String getDisplayIcon() { return displayIcon; }

    @Override
    public String toString() {
        return "Reward{type=" + type + ", value=" + value + ", displayName=" + displayName
                + (displayIcon != null ? ", displayIcon=" + displayIcon : "") + "}";
    }
}
