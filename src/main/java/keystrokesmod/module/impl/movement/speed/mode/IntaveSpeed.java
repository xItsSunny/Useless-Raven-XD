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
    private static final float NORMAL_TIMER_SPEED = 1.0f;
    private static final float FALLING_TIMER_SPEED = 0.8f;
    private static final float JUMP_TIMER_SPEED = 1.6f;

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

        adjustTimerSpeedBasedOnFallDistance(player);
    }

    private void adjustTimerSpeedBasedOnFallDistance(EntityPlayerSP player) {
        float fallDist = player.fallDistance;

        if (fallDist >= 1.3) {
            Utils.getTimer().timerSpeed = NORMAL_TIMER_SPEED; 
        } else if (fallDist > 0.1 && fallDist < 1.3) {
            Utils.getTimer().timerSpeed = FALLING_TIMER_SPEED; 
        } else if (!player.onGround && fallDist <= 0.1) {
            Utils.getTimer().timerSpeed = JUMP_TIMER_SPEED; 
        }
    }

    @Override
    public void onDisable() {
        Utils.getTimer().timerSpeed = NORMAL_TIMER_SPEED;
        Utils.resetTimer();
    }
}
