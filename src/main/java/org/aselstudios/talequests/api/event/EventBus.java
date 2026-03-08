package org.aselstudios.talequests.api.event;

/**
 * Event bus for subscribing to TaleQuests quest events.
 *
 * <p>Handlers are called synchronously on the thread that fires the event.
 * Exceptions thrown by a handler are logged and do not prevent other
 * handlers from being called.</p>
 *
 * <pre>{@code
 * api.getEventBus().subscribe(QuestCompleteEvent.class, event -> {
 *     System.out.println(event.getPlayerId() + " completed " + event.getQuestId());
 * });
 * }</pre>
 */
public interface EventBus {

    /**
     * Subscribes a handler for the given event type.
     *
     * @param eventType the event class to listen for
     * @param handler   the handler to invoke when the event fires
     * @param <T>       the event type
     */
    <T extends QuestEvent> void subscribe(Class<T> eventType, EventHandler<T> handler);

    /**
     * Unsubscribes a previously registered handler.
     *
     * @param eventType the event class
     * @param handler   the handler to remove
     * @param <T>       the event type
     */
    <T extends QuestEvent> void unsubscribe(Class<T> eventType, EventHandler<T> handler);

    /**
     * Removes all handlers for all event types.
     *
     * <p>Useful for plugin cleanup during {@code shutdown()}.
     * After calling this, no events will be delivered until new
     * handlers are subscribed.</p>
     */
    void unsubscribeAll();
}
