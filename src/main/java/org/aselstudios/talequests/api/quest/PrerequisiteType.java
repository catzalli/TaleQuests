package org.aselstudios.talequests.api.quest;

import java.util.Locale;
import java.util.Set;

/**
 * String constants for built-in prerequisite types.
 *
 * <p>These match the types used in config YAML and the typed prerequisite system.
 * Custom types beyond these can be used for future extensibility.</p>
 */
public final class PrerequisiteType {

    /** The player must have completed the specified quest. */
    public static final String QUEST_COMPLETE = "QUEST_COMPLETE";

    /** The player must have a minimum MmoSkillTree skill level (optional dependency). */
    public static final String SKILL_LEVEL = "SKILL_LEVEL";

    /** The player must have a minimum combined total skill level (optional dependency). */
    public static final String TOTAL_LEVEL = "TOTAL_LEVEL";

    /** The player must have the specified server permission node. */
    public static final String PERMISSION = "PERMISSION";

    private static final Set<String> BUILT_IN = Set.of(
            QUEST_COMPLETE, SKILL_LEVEL, PERMISSION, TOTAL_LEVEL
    );

    private PrerequisiteType() {}

    /** Returns {@code true} if the given type is a built-in prerequisite type. */
    public static boolean isBuiltIn(String type) {
        if (type == null) return false;
        return BUILT_IN.contains(type.toUpperCase(Locale.ROOT));
    }
}
