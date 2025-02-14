package keystrokesmod.module.impl.movement.speed.mode;

import keystrokesmod.event.player.PreUpdateEvent;
import keystrokesmod.module.impl.movement.Speed;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import keystrokesmod.eventbus.annotations.EventListener;
import net.minecraft.client.entity.EntityPlayerSP;

import static keystrokesmod.Client.mc;

import org.jetbrains.annotations.NotNull;

public class IntaveSpeed extends SubMode<Speed> {

    public IntaveSpeed(String name, @NotNull Speed parent) {
        super(name, parent);
    }

    @EventListener
    public void onPreUpdate(PreUpdateEvent event) {
        if (parent.noAction()) return;

        EntityPlayerSP player = mc.thePlayer;
        if (player == null || !MoveUtil.isMoving()) return;

        if (player.onGround && !player.isCollidedHorizontally) {
            player.jump();
        }

        float fallDist = player.fallDistance;
        if (fallDist >= 1.3) {
            Utils.getTimer().timerSpeed = 1.0f;
        } else if (fallDist > 0.1 && fallDist < 1.3) {
            Utils.getTimer().timerSpeed = 0.7f;
        } else if (!player.onGround && fallDist <= 0.1) {
            Utils.getTimer().timerSpeed = 1.4f;
        }
    }

    @Override
    public void onDisable() {
        Utils.getTimer().timerSpeed = 1.0f;
        Utils.resetTimer();
    }
}
