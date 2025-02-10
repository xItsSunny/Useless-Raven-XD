package keystrokesmod.module.impl.movement.speed.mode;

import keystrokesmod.event.player.MoveInputEvent;
import keystrokesmod.event.player.PreUpdateEvent;
import keystrokesmod.module.impl.movement.Speed;
import keystrokesmod.module.impl.other.RotationHandler;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import keystrokesmod.eventbus.annotations.EventListener;
import net.minecraft.client.entity.EntityPlayerSP;

public class IntaveSpeed extends SubMode<Speed> {

    @EventListener
    public void onPreUpdate(PreUpdateEvent event) {
        if (parent.noAction()) return;

        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return;

        if (!player.onGround) {
            float rotationOffset = player.moveStrafing > 0 ? 45f : -45f;
            RotationHandler.setRotationYaw(player.rotationYaw + rotationOffset);
            RotationHandler.setMoveFix(RotationHandler.MoveFix.Silent);
        } else {
            player.motionY = 0.4;
        }

        Utils.getTimer().timerSpeed = 1.004f;
    }

    @EventListener
    public void onMove(MoveInputEvent event) {
        if (MoveUtil.isMoving()) {
            event.setJump(true);
        }
    }

    @Override
    public void onDisable() {
        Utils.resetTimer();
    }
}
