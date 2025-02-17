package keystrokesmod.module.impl.movement.speed.hypixel.lowhop;

import keystrokesmod.event.player.PreUpdateEvent;
import keystrokesmod.module.impl.movement.speed.setting.HypixelLowHopSpeed;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

public class HypixelLowHopMotionSpeed extends SubMode<HypixelLowHopSpeed> {
    private final Impl impl = new Impl("Impl", parent);
    private boolean toDisable = false;

    public HypixelLowHopMotionSpeed(String name, @NotNull HypixelLowHopSpeed parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() throws Throwable {
        impl.enable();
        toDisable = false;
    }

    @Override
    public void onDisable() throws Throwable {
        toDisable = true;
    }

    public class Impl extends SubMode<HypixelLowHopSpeed> {
        private int offGroundTicks = 0;

        public Impl(String name, @NotNull HypixelLowHopSpeed parent) {
            super(name, parent);
        }

        @Override
        public void onEnable() {
            offGroundTicks = 999;
        }

        @EventListener
        public void onPreUpdate(PreUpdateEvent event) {
            if (mc.thePlayer.onGround) {
                offGroundTicks = 0;
            } else {
                offGroundTicks++;
            }

            if (!MoveUtil.isMoving() || parent.parent.parent.noAction()) return;

            if (offGroundTicks == 0) {
                if (toDisable) {
                    disable();
                } else {
                    MoveUtil.strafe(MoveUtil.getAllowedHorizontalDistance() - Math.random() / 100f);
                    MoveUtil.jump();
                }
            } else if (parent.noLowHop() || MoveUtil.getJumpEffect() != 0) {
                return;
            }

            switch (offGroundTicks) {
                case 1:
                    mc.thePlayer.motionY = 0.39;
                    break;
                case 3:
                    mc.thePlayer.motionY -= 0.13;
                    break;
                case 4:
                    mc.thePlayer.motionY -= 0.2;
                    break;
            }
        }
    }
}
