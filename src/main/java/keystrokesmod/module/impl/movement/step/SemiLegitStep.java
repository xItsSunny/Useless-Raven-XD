package keystrokesmod.module.impl.movement.step;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import keystrokesmod.event.player.PreMotionEvent;
import keystrokesmod.event.player.StepEvent;
import keystrokesmod.module.impl.movement.Step;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

public class SemiLegitStep extends SubMode<Step> {
    public static final DoubleList MOTION = DoubleList.of(.41999998688698, .7531999805212, 1.001335997911214, 1.16610926093821, 1.24918707874468, 1.093955074228084);

    private final SliderSetting delay;
    private final SliderSetting timer;

    private State state = State.NONE;
    private double x, y, z;
    private long lastStep = -1;

    public SemiLegitStep(String name, @NotNull Step parent) {
        super(name, parent);
        this.registerSetting(delay = new SliderSetting("Delay", 1000, 0, 5000, 250, "ms"));
        this.registerSetting(timer = new SliderSetting("Timer", 0.16, 0.1, 1, 0.01));
    }

    @Override
    public void onDisable() throws Throwable {
        mc.thePlayer.stepHeight = 0.6f;
        state = State.NONE;
    }

    @EventListener
    public void onStep(@NotNull StepEvent event) {
        if (event.getHeight() == 1) {
            state = State.BALANCE;
            Utils.getTimer().timerSpeed = (float) timer.getInput();
            mc.thePlayer.stepHeight = 0.6f;

            x = mc.thePlayer.lastTickPosX;
            y = mc.thePlayer.posY;
            z = mc.thePlayer.lastTickPosZ;
            lastStep = System.currentTimeMillis();
        }
    }

    @EventListener(priority = -1)
    public void onPreMotion(PreMotionEvent event) {
        if (System.currentTimeMillis() - lastStep > delay.getInput())
            mc.thePlayer.stepHeight = 1;

        switch (state) {
            case BALANCE:
                event.cancel();
                MoveUtil.stop();
                state = State.STEP;
                break;
            case STEP:
                for (double motion : MOTION) {
                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + motion, z, false));
                }
                Utils.resetTimer();
                state = State.NONE;
                break;
        }
    }

    private enum State {
        NONE,
        BALANCE,
        STEP
    }
}
