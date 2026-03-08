package org.aselstudios.talequests.api.event;

/**
 * Functional interface for handling TaleQuests events.
 *
 * @param <T> the event type
 * @see EventBus#subscribe(Class, EventHandler)
 */
@FunctionalInterface
public interface EventHandler<T extends QuestEvent> {

    /**
     * Called when the event is fired.
     *
     * @param event the event instance
     */
    void handle(T event);
}
