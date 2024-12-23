package keystrokesmod.utility.render.progress;

import keystrokesmod.Client;
import keystrokesmod.event.render.Render2DEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProgressManager {
    private static final Queue<Progress> progresses = new ConcurrentLinkedQueue<>();

    static {
        Client.EVENT_BUS.register(Render2DEvent.class, () -> progresses.forEach(Progress::render));
    }

    public static void add(@NotNull Progress progress) {
        if (progresses.add(progress)) {
            progress.setPosY(progresses.size());
        }
    }

    public static void remove(@NotNull Progress progress) {
        if (progresses.remove(progress)) {
            int posY = progress.getPosY();
            progress.setPosY(0);

            for (Progress p : progresses) {
                if (p.getPosY() > posY)
                    p.setPosY(p.getPosY() - 1);
            }
        }
    }
}
