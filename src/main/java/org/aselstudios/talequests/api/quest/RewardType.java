package org.aselstudios.talequests.api.quest;

import java.util.Locale;
import java.util.Set;

/**
 * String constants for built-in quest reward types.
 *
 * <p>Custom reward types can be any {@code UPPER_CASE} string
 * (e.g. {@code "XP"}, {@code "SKILL_POINTS"}). Register a
 * {@link org.aselstudios.talequests.api.extension.CustomRewardHandler}
 * to handle delivery of custom reward types.</p>
 */
public final class RewardType {

    /** Economy deposit. Value is the amount (e.g. {@code "500"}). */
    public static final String MONEY = "MONEY";

    /** Console command execution. Value is the command string. */
    public static final String COMMAND = "COMMAND";

    /** Item reward. Value is {@code "itemId"} or {@code "itemId:amount"}. */
    public static final String ITEM = "ITEM";

    private static final Set<String> BUILT_IN = Set.of(MONEY, COMMAND, ITEM);

    private RewardType() {}

    /**
     * Returns {@code true} if the given type is one of the three built-in reward types.
     *
     * @param type the type string (case-insensitive)
     */
    public static boolean isBuiltIn(String type) {
        if (type == null) return false;
        return BUILT_IN.contains(type.toUpperCase(Locale.ROOT));
    }
}
