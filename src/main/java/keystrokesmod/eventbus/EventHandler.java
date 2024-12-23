package keystrokesmod.eventbus;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;
import tech.skidonion.obfuscator.annotations.NativeObfuscation.Inline;

import java.lang.reflect.Method;
import java.util.function.Consumer;

@AllArgsConstructor
public final class EventHandler<T extends Event> {
    private final @Nullable Method method;
    private final Class<T> eventType;
    private final Consumer<T> eventConsumer;
    private final int priory;

    @Inline
    public @Nullable Method getMethod() {
        return method;
    }

    @Inline
    public Class<T> getEventType() {
        return eventType;
    }

    @Inline
    public Consumer<T> getEventConsumer() {
        return eventConsumer;
    }

    @Inline
    public int getPriory() {
        return priory;
    }

    @Inline
    public void invoke(T event) {
        eventConsumer.accept(event);
    }
}
