package net.game.spacepirates.data.messaging;

import net.game.spacepirates.data.messaging.api.Message;
import net.game.spacepirates.data.messaging.api.Topic;
import net.game.spacepirates.data.messaging.impl.BaseMessage;
import net.game.spacepirates.data.messaging.impl.BaseTopic;
import net.game.spacepirates.util.TaskUtils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class MessageBus {

    private static MessageBus instance;
    protected final Queue<Message> messageQueue;
    protected final Map<Topic, List<Consumer>> listeners;

    protected Consumer<Message> messageInterceptor;

    public MessageBus() {
        messageQueue = new ConcurrentLinkedQueue<>();
        listeners = new HashMap<>();
    }

    public static MessageBus get() {
        if (instance == null) {
            instance = new MessageBus();
        }
        return instance;
    }

    public void addInterceptor(Consumer<Message> task) {
        if(messageInterceptor == null) {
            messageInterceptor = task;
        }else{
            messageInterceptor.andThen(task);
        }
    }

    private void intercept(Message msg) {
        if(messageInterceptor != null) {
            messageInterceptor.accept(msg);
        }
    }

    public <T> void dispatch(String ref, T data) {
        messageQueue.add(new BaseMessage<>(ref, data));
    }

    public void dispatch(Message msg) {
        messageQueue.add(msg);
    }

    public void update() {
        Queue<Message> tmp;
        synchronized (messageQueue) {
            tmp = new LinkedList<>(messageQueue);
            messageQueue.clear();
        }

        while (!tmp.isEmpty()) {
            Message msg = tmp.poll();
            intercept(msg);
            notify(msg.getRef(), msg.getData());
        }
    }

    public <T> void subscribe(Topic<T> topic, Consumer<T> task) {
        getConsumers(topic).add(task);
    }

    public <T> Topic<T> getTopic(String ref, Class<T> type) {
        return new BaseTopic<>(ref, type);
    }

    @SuppressWarnings("unchecked")
    private <T> void notify(String ref, T data) {
        Topic topic = getTopic(ref, data.getClass());
        List<Consumer> consumers = getConsumers(topic);
        for (Consumer consumer : consumers) {
            TaskUtils.safeRun(() -> consumer.accept(data));
        }
    }

    private List<Consumer> getConsumers(Topic topic) {
        if (!listeners.containsKey(topic)) {
            listeners.put(topic, new ArrayList<>());
        }
        return listeners.get(topic);
    }

}
