package keystrokesmod.module.impl.world.scaffold.rotation;

import keystrokesmod.event.player.RotationEvent;
import keystrokesmod.module.impl.world.Scaffold;
import keystrokesmod.module.impl.world.scaffold.IScaffoldRotation;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.aim.AimSimulator;
import keystrokesmod.utility.aim.RotationData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class HypixelRotation extends IScaffoldRotation {
    public HypixelRotation(String name, @NotNull Scaffold parent) {
        super(name, parent);
    }

    @Override
    public @NotNull RotationData onRotation(float placeYaw, float placePitch, boolean forceStrict, @NotNull RotationEvent event) {
        float left = RotationUtils.normalize(parent.getYaw() - 70);
        float right = RotationUtils.normalize(parent.getYaw() + 70);
        float yaw = RotationUtils.normalize(placeYaw);

        if (AimSimulator.yawEquals(yaw, left) || AimSimulator.yawEquals(yaw, right)) {
            return new RotationData(placeYaw, placePitch);
        }

        // to ensure '-180 < (yaw) < left < _ < right < (yaw) < 180'
        if (left < right) {
            // left < (range) < right
            if (yaw > left && yaw < right) {
                float deltaLeft = yaw - left;
                float deltaRight = right - yaw;

                return new RotationData(
                        moveToNearest(placeYaw, deltaLeft, deltaRight, left, right), placePitch);
            }
        } else {
            // (range) < right < _ < left < (range)
            if (yaw < right) {
                // (range) < right < _ < left
                float deltaLeft = yaw + 360 - left;
                float deltaRight = right - yaw;

                return new RotationData(
                        moveToNearest(placeYaw, deltaLeft, deltaRight, left, right), placePitch);
            } else if (yaw > left) {
                // right < _ < left < (range)
                float deltaLeft = yaw - left;
                float deltaRight = -yaw + 180 + right;

                return new RotationData(
                        moveToNearest(placeYaw, deltaLeft, deltaRight, left, right), placePitch);
            }
        }

        return new RotationData(placeYaw, placePitch);
    }

    @Contract(pure = true)
    private static float moveToNearest(float value, float deltaLeft, float deltaRight, float left, float right) {
        if (deltaLeft < deltaRight) {
            // move to left
            return AimSimulator.rotMove(left, value, 180, AimSimulator.getGCD(), 180);
        } else {
            // move to right
            return AimSimulator.rotMove(right, value, 180, AimSimulator.getGCD(), 180);
        }
    }
}
