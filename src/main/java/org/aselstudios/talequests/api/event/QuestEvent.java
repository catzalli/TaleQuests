package org.aselstudios.talequests.api.event;

/**
 * Base class for all TaleQuests API events.
 *
 * <p>Not all events are cancellable. Subclasses that support cancellation
 * expose their own {@code isCancelled()} and {@code setCancelled()} methods.</p>
 */
public abstract class QuestEvent {

    QuestEvent() {}
}
