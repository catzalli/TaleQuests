package org.aselstudios.talequests.api.extension;

import java.util.UUID;

/**
 * Handler for delivering custom quest reward types.
 *
 * <p>Register via
 * {@link org.aselstudios.talequests.api.TaleQuestsProvider#registerRewardHandler}.
 * When a quest with a matching reward type is completed — whether it was
 * registered via the API or configured in YAML with {@code type: CUSTOM}
 * and a matching {@code custom_reward_type} — this handler is called to
 * deliver the reward.</p>
 *
 * <p><b>API quest example:</b></p>
 * <pre>{@code
 * // 1. Register the handler
 * api.registerRewardHandler("XP", (playerId, value, displayName) -> {
 *     MyXPSystem.addXP(playerId, Integer.parseInt(value));
 * });
 *
 * // 2. Use in a quest
 * Quest.builder("myplugin:grind")
 *     .reward("XP", "50", "50 XP")
 *     .build();
 * }</pre>
 *
 * <p><b>YAML config quest example:</b></p>
 * <pre>{@code
 * rewards:
 *   - type: CUSTOM
 *     custom_reward_type: "XP"
 *     value: "50"
 *     display_name: "50 XP"
 * }</pre>
 */
@FunctionalInterface
public interface CustomRewardHandler {

    /**
     * Delivers a custom reward to the player.
     *
     * <p>Called on the thread that completed the quest. The player is
     * guaranteed to be online when this method is invoked.</p>
     *
     * @param playerId    the player's UUID
     * @param value       the reward value string (meaning depends on your reward type)
     * @param displayName the display name configured for this reward
     */
    void deliver(UUID playerId, String value, String displayName);
}
