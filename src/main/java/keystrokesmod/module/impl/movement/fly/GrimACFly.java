package keystrokesmod.module.impl.movement.fly;

import keystrokesmod.event.player.PreMotionEvent;
import keystrokesmod.module.impl.movement.Fly;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

public class GrimACFly extends SubMode<Fly> {
    private double lastY;

    public GrimACFly(String name, @NotNull Fly parent) {
        super(name, parent);
    }

    @EventListener(priority = -1)
    public void onPreMotion(@NotNull PreMotionEvent event) {
        lastY += 0.001;
        mc.thePlayer.setPosition(event.getPosX(), lastY, event.getPosZ());
        event.setPosY(lastY);
        event.setOnGround(true);
    }

    @Override
    public void onEnable() throws Throwable {
        lastY = mc.thePlayer.posY;
    }
}
