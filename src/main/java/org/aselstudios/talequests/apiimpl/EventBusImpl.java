package org.aselstudios.talequests.apiimpl;

import org.aselstudios.talequests.api.event.EventBus;
import org.aselstudios.talequests.api.event.EventHandler;
import org.aselstudios.talequests.api.event.QuestEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBusImpl implements EventBus {

    private static final Logger LOGGER = Logger.getLogger("TaleQuests");

    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<EventHandler<?>>> handlers =
            new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends QuestEvent> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        if (eventType == null) throw new IllegalArgumentException("eventType must not be null");
        if (handler == null) throw new IllegalArgumentException("handler must not be null");
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @Override
    public <T extends QuestEvent> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        if (eventType == null || handler == null) return; // lenient for unsubscribe
        CopyOnWriteArrayList<EventHandler<?>> list = handlers.get(eventType);
        if (list != null) {
            list.remove(handler);
        }
    }

    @Override
    public void unsubscribeAll() {
        handlers.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends QuestEvent> void fire(T event) {
        if (event == null) return;
        Class<?> clazz = event.getClass();
        while (clazz != null && QuestEvent.class.isAssignableFrom(clazz)) {
            CopyOnWriteArrayList<EventHandler<?>> list = handlers.get(clazz);
            if (list != null) {
                for (EventHandler<?> raw : list) {
                    try {
                        ((EventHandler<T>) raw).handle(event);
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING,
                                "Exception in TaleQuests event handler for " + event.getClass().getSimpleName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
