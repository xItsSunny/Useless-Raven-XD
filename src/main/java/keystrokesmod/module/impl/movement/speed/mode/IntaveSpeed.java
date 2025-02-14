package keystrokesmod.module.impl.movement.speed.mode;

import keystrokesmod.event.player.MoveInputEvent;
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

        if (player.onGround) {
            player.motionY = 0.31;
            MoveUtil.strafe(MoveUtil.getBaseSpeed() * 1.01); 
        } else {
            player.motionY -= 0.001;
            MoveUtil.strafe(MoveUtil.getBaseSpeed() * 1.00); 
        }
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
