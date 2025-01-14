package keystrokesmod.module.impl.world.scaffold.sprint;

import keystrokesmod.event.player.PrePlayerInputEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.exploit.disabler.hypixel.HypixelMotionDisabler;
import keystrokesmod.module.impl.world.Scaffold;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

public class HypixelJump2Sprint extends VanillaSprint {
    private final ButtonSetting lowHop;

    public HypixelJump2Sprint(String name, @NotNull Scaffold parent) {
        super(name, parent);
        this.registerSetting(lowHop = new ButtonSetting("Low hop", true));
    }

    @EventListener
    public void onPrePlayerInput(PrePlayerInputEvent event) {
        if (!MoveUtil.isMoving() || ModuleManager.tower.canTower()) return;
        if (parent.offGroundTicks == 0) {
            MoveUtil.strafe(Math.min(MoveUtil.getAllowedHorizontalDistance(), MoveUtil.speed() * 2) - Math.random() / 100f);
            MoveUtil.jump();
        } else if (parent.offGroundTicks == 5
                && lowHop.isToggled() && HypixelMotionDisabler.isDisabled()) {
            mc.thePlayer.motionY = MoveUtil.predictedMotion(mc.thePlayer.motionY, 2);
        }
    }
}