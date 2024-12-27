package keystrokesmod.module.impl.movement.speed;

import keystrokesmod.event.player.PreUpdateEvent;
import keystrokesmod.module.impl.movement.Speed;
import keystrokesmod.module.impl.movement.TargetStrafe;
import keystrokesmod.module.impl.movement.speed.hypixel.GroundStrafeSpeed;
import keystrokesmod.module.impl.movement.speed.hypixel.HypixelGroundSpeed;
import keystrokesmod.module.impl.movement.speed.hypixel.HypixelLowHopSpeed;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeValue;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.movement.Move;
import keystrokesmod.eventbus.annotations.EventListener;
import net.minecraft.util.MathHelper;
import org.jetbrains.annotations.NotNull;

public class HypixelSpeed extends SubMode<Speed> {
    private final ModeValue mode;
    private final ButtonSetting strafe;
    private final ButtonSetting fastStrafe;
    private final SliderSetting minAngle;
    private final ButtonSetting fastStop;

    private double lastAngle;
    private double lastLastTickPosX = Double.NaN;
    private double lastLastTickPosZ = Double.NaN;

    public HypixelSpeed(String name, @NotNull Speed parent) {
        super(name, parent);
        this.registerSetting(mode = new ModeValue("Hypixel mode", this)
                .add(new GroundStrafeSpeed("GroundStrafe", this))
                .add(new HypixelGroundSpeed("Ground", this))
                .add(new HypixelLowHopSpeed("LowHop", this))
        );
        this.registerSetting(strafe = new ButtonSetting("Strafe", false));
        this.registerSetting(fastStrafe = new ButtonSetting("Fast strafe", false, strafe::isToggled));
        this.registerSetting(minAngle = new SliderSetting("Min angle", 30, 15, 90, 15, strafe::isToggled));
        this.registerSetting(fastStop = new ButtonSetting("Fast stop", false));
    }

    @EventListener
    public void onPreUpdate(PreUpdateEvent event) {
        if (fastStop.isToggled() && !Double.isNaN(lastLastTickPosX) && !Double.isNaN(lastLastTickPosZ)) {
            double speed = Math.hypot(
                    (mc.thePlayer.motionX - (mc.thePlayer.lastTickPosX - lastLastTickPosX)),
                    (mc.thePlayer.motionZ - (mc.thePlayer.lastTickPosZ - lastLastTickPosZ)));
            if (speed < 0.0125) {
                MoveUtil.strafe();
            }
        }

        if (strafe.isToggled() && MoveUtil.isMoving()) {
            if (fastStrafe.isToggled()) {
                double attemptAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction()));
                double movementAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(
                        Math.atan2(mc.thePlayer.motionZ, mc.thePlayer.motionX)
                ) - minAngle.getInput());

                if (wrappedDifference(attemptAngle, movementAngle) > minAngle.getInput()) {
                    MoveUtil.strafe(MoveUtil.speed(), (float) movementAngle - 180);
                }
                return;
            }

            if (canStrafe()) {
                if (parent.offGroundTicks == 9) {
                    MoveUtil.strafe(Math.min(0.2, MoveUtil.speed()));
                    mc.thePlayer.motionY += 0.1;
                } else {
                    MoveUtil.strafe(Math.min(0.11, MoveUtil.speed()));
                }
            }
        }

        lastLastTickPosX = mc.thePlayer.lastTickPosX;
        lastLastTickPosZ = mc.thePlayer.lastTickPosZ;
    }

    private static double wrappedDifference(double number1, double number2) {
        return Math.min(Math.abs(number1 - number2), Math.min(Math.abs(number1 - 360) - Math.abs(number2 - 0), Math.abs(number2 - 360) - Math.abs(number1 - 0)));
    }

    private boolean canStrafe() {
        if (mc.thePlayer.onGround || !MoveUtil.isMoving())
            return false;
        final double curAngle = Move.fromMovement(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing).getDeltaYaw()
                + TargetStrafe.getMovementYaw();

        if (Math.abs(curAngle - lastAngle) < minAngle.getInput() || (mc.thePlayer.hurtTime < 7 && mc.thePlayer.hurtTime > 0))
            return false;
        lastAngle = curAngle;

        return parent.offGroundTicks == 1 || (parent.offGroundTicks >= 4 && parent.offGroundTicks <= 9);
    }

    @Override
    public void onEnable() {
        mode.enable();
        lastAngle = MoveUtil.direction() * (180 / Math.PI);
    }

    @Override
    public void onDisable() {
        mode.disable();
        lastLastTickPosX = Double.NaN;
        lastLastTickPosZ = Double.NaN;
    }
}
