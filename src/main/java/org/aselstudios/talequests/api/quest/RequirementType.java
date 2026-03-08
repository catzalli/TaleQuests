package org.aselstudios.talequests.api.quest;

import java.util.Locale;
import java.util.Set;

/**
 * String constants for built-in quest requirement types.
 *
 * <p>Custom requirement types can be any {@code UPPER_CASE} string
 * (e.g. {@code "FISHING"}, {@code "VISIT_BIOME"}).
 * Use these constants for the eight built-in types that are
 * automatically tracked by TaleQuests listeners.</p>
 */
public final class RequirementType {

    public static final String BREAK_BLOCK = "BREAK_BLOCK";
    public static final String PLACE_BLOCK = "PLACE_BLOCK";
    public static final String USE_BLOCK = "USE_BLOCK";
    public static final String KILL_MOB = "KILL_MOB";
    public static final String KILL_PLAYER = "KILL_PLAYER";
    public static final String HAVE_ITEM = "HAVE_ITEM";
    public static final String CRAFT_ITEM = "CRAFT_ITEM";
    public static final String CHAT_MESSAGE = "CHAT_MESSAGE";

    private static final Set<String> BUILT_IN = Set.of(
            BREAK_BLOCK, PLACE_BLOCK, USE_BLOCK, KILL_MOB, KILL_PLAYER,
            HAVE_ITEM, CRAFT_ITEM, CHAT_MESSAGE
    );

    private RequirementType() {}

    /**
     * Returns {@code true} if the given type is one of the eight built-in requirement types.
     *
     * @param type the type string (case-insensitive)
     */
    public static boolean isBuiltIn(String type) {
        if (type == null) return false;
        return BUILT_IN.contains(type.toUpperCase(Locale.ROOT));
    }
}
