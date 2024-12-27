package keystrokesmod.eventbus;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import keystrokesmod.eventbus.annotations.EventListener;
import keystrokesmod.utility.ReflectionUtils;
import keystrokesmod.utility.Utils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 高效的同步事件总线
 * 不支持监听父事件
 */
@Getter
@SuppressWarnings("unused")
public final class EventBus {
    private final Map<Class<? extends Event>, List<EventHandler<? extends Event>>> eventHandlers
            = new Object2ObjectOpenHashMap<>(10);

    /**
     * 向事件总线注册对象的所有带有 {@link EventListener} 注解的方法
     *
     * @param object 需要注册的对象
     */
    @SuppressWarnings("unchecked")
    public <T> void register(@NotNull T object) {
        final Class<T> objClass = (Class<T>) object.getClass();

        doRegister(objClass, object);
    }

    /**
     * 向事件总线注册类的所有带有 {@link EventListener} 注解的静态方法
     *
     * @param objClass 需要注册的类
     */
    public <T> void register(@NotNull Class<T> objClass) {
        doRegister(objClass, null);
    }

    @SuppressWarnings("unchecked")
    private <T> void doRegister(@NotNull Class<T> objClass, @Nullable T object) {
        for (Method method : objClass.getDeclaredMethods()) {
            boolean found = false;
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                if (annotation.annotationType() == EventListener.class) {
                    found = true;
                    break;
                }
            }
            if (!found) continue;

            Parameter[] parameters = method.getParameters();
            if (parameters.length != 1) {
                throw new InvalidParameterException(
                        String.format("Method must have one parameter with Event type. (%s in %s)",
                                method.getName(), objClass.getName()));
            }

            final Class<Event> type;
            final Class<?> rawType = parameters[0].getType();
            if (!Event.class.isAssignableFrom(rawType)) {
                throw new InvalidParameterException(
                        String.format("Method must have one parameter with Event type. (%s in %s)",
                                method.getName(), objClass.getName()));
            }
            type = (Class<Event>) rawType;

            Consumer<Event> eventConsumer;

            try {
                // 0 reflect method
                eventConsumer = ReflectionUtils.getFastMethod(objClass, object, method, type);
            } catch (Throwable e) {
                // revert to method invoke
                method.setAccessible(true);
                eventConsumer = event -> {
                    try {
                        method.invoke(object, event);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        Utils.sendMessage(String.format("Fail to post event '%s' to '%s'",
                                event.getClass().getName(), objClass.getName()));
                    }
                };
            }

            addHandler(new EventHandler<>(method, type, eventConsumer,
                    method.getAnnotation(EventListener.class).priority()));
        }
    }

    /**
     * 向事件总线注册runnable
     *
     * @param eventType 事件类型
     * @param runnable  需要注册的方法
     */
    public <T extends Event> void register(@NotNull Class<T> eventType, @NotNull Runnable runnable) {
        register(eventType, event -> runnable.run());
    }

    /**
     * 向事件总线注册consumer
     *
     * @param eventType 事件类型
     * @param consumer 需要注册的方法
     */
    public <T extends Event> void register(@NotNull Class<T> eventType, @NotNull Consumer<@NotNull T> consumer) {
        addHandler(new EventHandler<>(null, eventType, consumer, 0));
    }

    private void addHandler(@NotNull EventHandler<? extends Event> eventHandler) {
        synchronized (eventHandlers) {
            final List<EventHandler<? extends Event>> prioryList;
            Class<? extends Event> type = eventHandler.getEventType();
            if (!eventHandlers.containsKey(type)) {
                prioryList = new ObjectArrayList<>(1);
                eventHandlers.put(type, prioryList);
            } else {
                prioryList = eventHandlers.get(type);
            }

            int addToIndex = 0;
            for (int i = 0; i < prioryList.size(); i++) {
                if (prioryList.get(i).getPriory() < eventHandler.getPriory()) {
                    addToIndex = i;
                    break;
                }
            }
            prioryList.add(addToIndex, eventHandler);
        }
    }

    /**
     * 从事件总线取消注册对象的所有带有 {@link EventListener} 注解的方法
     * @param object 需要取消注册的对象
     */
    public void unregister(Object object) {
        if (object == null) return;

        final Set<Method> methods = new ObjectOpenHashSet<>(object.getClass().getDeclaredMethods());
        synchronized (eventHandlers) {
            for (List<EventHandler<? extends Event>> list : eventHandlers.values()) {
                list.removeIf(eventHandler -> methods.contains(eventHandler.getMethod()));
            }
        }
    }


    /**
     * 从事件总线取消注册runnable
     *
     * @param runnable 需要取消注册的方法
     */
    public void unregister(@NotNull Runnable runnable) {
        synchronized (eventHandlers) {
            for (List<EventHandler<? extends Event>> list : eventHandlers.values()) {
                list.removeIf(eventHandler -> eventHandler.getEventConsumer().equals(runnable));
            }
        }
    }

    /**
     * 从事件总线取消注册consumer
     *
     * @param consumer 需要取消注册的方法
     */
    public void unregister(@NotNull Consumer<? extends Event> consumer) {
        synchronized (eventHandlers) {
            for (List<EventHandler<? extends Event>> list : eventHandlers.values()) {
                list.removeIf(eventHandler -> eventHandler.getEventConsumer().equals(consumer));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void post(T event) {
        if (event == null) return;

        List<EventHandler<? extends Event>> handlers = eventHandlers.get(event.getClass());
        if (handlers == null) return;

        for (EventHandler<? extends Event> eventHandler : handlers) {
            try {
                ((EventHandler<T>) eventHandler).invoke(event);
            } catch (Throwable e) {
                Method method = eventHandler.getMethod();
                String targetName;
                if (method != null) {
                    targetName = String.format("%s->%s", method.getDeclaringClass().getSimpleName(), method.getName());
                    if (Modifier.isNative(method.getModifiers()))
                        targetName += " <native method>";
                } else {
                    targetName = "Unknown Source";
                }
                Utils.sendMessage(String.format("Exception '%s' while post event '%s' to '%s': %s",
                        e.getClass().getSimpleName(), event.getClass().getSimpleName(), targetName, e.getMessage()));
            }

            if (event instanceof CancellableEvent)
                if (((CancellableEvent) event).isCancelled()) return;
        }
    }
}
