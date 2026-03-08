package org.aselstudios.talequests.api.quest;

import java.util.Locale;

/**
 * An immutable quest requirement definition.
 *
 * <p>Each requirement specifies a type (built-in or custom), a target
 * identifier, the amount needed for completion, and whether to use
 * exact or contains matching for the target.</p>
 */
public final class Requirement {

    private final String type;
    private final String target;
    private final int amount;
    private final boolean exactMatch;

    /**
     * Creates a new requirement with exact matching (default).
     *
     * @param type   the requirement type, normalized to UPPER_CASE (e.g. {@code "KILL_MOB"} or {@code "FISHING"})
     * @param target the target identifier (e.g. mob ID, item ID, or {@code "*"} for wildcard)
     * @param amount the required amount (must be positive)
     */
    public Requirement(String type, String target, int amount) {
        this(type, target, amount, true);
    }

    /**
     * Creates a new requirement with configurable matching mode.
     *
     * @param type       the requirement type, normalized to UPPER_CASE
     * @param target     the target identifier, or {@code "*"} for wildcard
     * @param amount     the required amount (must be positive)
     * @param exactMatch {@code true} for exact matching, {@code false} for contains matching
     */
    public Requirement(String type, String target, int amount, boolean exactMatch) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be null or blank");
        }
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("target must not be null or blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive, got: " + amount);
        }
        this.type = type.toUpperCase(Locale.ROOT);
        this.target = target;
        this.amount = amount;
        this.exactMatch = exactMatch;
    }

    /** Returns the requirement type, always in UPPER_CASE. */
    public String getType() { return type; }

    /** Returns the target identifier. */
    public String getTarget() { return target; }

    /** Returns the required amount. */
    public int getAmount() { return amount; }

    /** Returns {@code true} if exact matching is used, {@code false} for contains matching. */
    public boolean isExactMatch() { return exactMatch; }

    @Override
    public String toString() {
        return "Requirement{type=" + type + ", target=" + target + ", amount=" + amount
                + ", exactMatch=" + exactMatch + "}";
    }
}
