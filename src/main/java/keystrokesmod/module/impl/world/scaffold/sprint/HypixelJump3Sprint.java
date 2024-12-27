package keystrokesmod.module.impl.world.scaffold.sprint;

import keystrokesmod.module.impl.world.Scaffold;
import keystrokesmod.module.impl.world.scaffold.IScaffoldSchedule;
import keystrokesmod.utility.aim.RotationData;
import org.jetbrains.annotations.NotNull;

public class HypixelJump3Sprint extends JumpSprint {
    private boolean lastLeft = false;

    public HypixelJump3Sprint(String name, @NotNull Scaffold parent) {
        super(name, parent);
    }

    @Override
    public RotationData onFinalRotation(@NotNull RotationData data) {
        boolean noRotation = ((IScaffoldSchedule) parent.schedule.getSelected()).noRotation();
        float left = parent.getYaw() - 70;
        float right = parent.getYaw() + 70;

        if (noRotation) {
            return new RotationData(lastLeft ? left : right, 78.41111119f);
        }

        float yaw = data.getYaw();
        if ((yaw > left && yaw < right) && !parent.isDiagonal()) {
            float diffLeft = Math.abs(left - yaw);
            float diffRight = Math.abs(yaw - right);
            if (diffLeft < diffRight) {
                lastLeft = true;
                return new RotationData(left, data.getPitch());
            } else {
                lastLeft = false;
                return new RotationData(right, data.getPitch());
            }
        }
        return data;
    }
}
