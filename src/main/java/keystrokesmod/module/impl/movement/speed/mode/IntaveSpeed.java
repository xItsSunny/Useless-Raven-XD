package keystrokesmod.module.impl.movement.speed.mode;

import keystrokesmod.event.player.MoveInputEvent;
import keystrokesmod.event.player.PreUpdateEvent;
import keystrokesmod.module.impl.movement.Speed;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import keystrokesmod.eventbus.annotations.EventListener;
import net.minecraft.client.entity.EntityPlayerSP;
import org.jetbrains.annotations.NotNull;

public class IntaveSpeed extends SubMode<Speed> {
    private final ButtonSetting lowHop;

    public IntaveSpeed(String name, @NotNull Speed parent) {
        super(name, parent);
        this.registerSetting(lowHop = new ButtonSetting("LowHop", false));
    }

    @EventListener
    public void onPreUpdate(PreUpdateEvent event) {
        if (parent.noAction()) return;

        EntityPlayerSP player = mc.thePlayer;
        if (player == null || !MoveUtil.isMoving()) return;

        if (player.onGround) {
            if (lowHop.isToggled()) {
                player.motionY = 0.3; 
                MoveUtil.setSpeed(MoveUtil.getBaseSpeed() * 1.05);
            }
        } else {
            player.motionY -= 0.0008;
            MoveUtil.setSpeed(MoveUtil.getBaseSpeed() * 1.02);
        }
    }

    @EventListener
    public void onMove(MoveInputEvent event) {
        if (MoveUtil.isMoving() && lowHop.isToggled()) {
            event.setJump(true); 
        }
    }

    @Override
    public void onDisable() {
        Utils.resetTimer();
    }
}
