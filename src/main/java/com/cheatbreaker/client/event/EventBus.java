package com.cheatbreaker.client.event;

import com.cheatbreaker.client.event.Event;
import com.cheatbreaker.client.util.Logging;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    public ConcurrentHashMap<Class<? extends Event>, CopyOnWriteArrayList<Consumer>> listeners = new ConcurrentHashMap();

    public <T extends Event> boolean checkClass(Class<T> clazz, Consumer<T> consumer) {
        CopyOnWriteArrayList<Consumer> copyOnWriteArrayList = this.listeners.get(clazz);
        return copyOnWriteArrayList != null && copyOnWriteArrayList.remove(consumer);
    }

    public <T extends Event> boolean checkClass2(Class<T> clazz2, Consumer<T> consumer) {
        return this.listeners.computeIfAbsent(clazz2, clazz -> new CopyOnWriteArrayList()).add(consumer);
    }

    public void handleEvent(Event event) {
        try {
            for (Class<?> clazz = event.getClass(); clazz != null && clazz != Event.class; clazz = clazz.getSuperclass()) {
                CopyOnWriteArrayList<Consumer> copyOnWriteArrayList = this.listeners.get(clazz);
                if (copyOnWriteArrayList == null) continue;
                copyOnWriteArrayList.forEach(consumer -> consumer.accept(event));
            }
        }
        catch (Exception exception) {
            Logging.error("EventBus [" + event.getClass() + "]", new Object[0]);
            exception.printStackTrace();
        }
    }
}
