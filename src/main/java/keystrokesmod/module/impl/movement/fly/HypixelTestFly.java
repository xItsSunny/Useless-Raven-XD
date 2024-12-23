package keystrokesmod.module.impl.movement.fly;

import keystrokesmod.event.player.MoveInputEvent;
import keystrokesmod.event.player.PreMotionEvent;
import keystrokesmod.event.network.ReceivePacketEvent;
import keystrokesmod.event.world.WorldChangeEvent;
import keystrokesmod.module.impl.movement.Fly;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

public class HypixelTestFly extends SubMode<Fly> {
    private final SliderSetting speed;
    private final ButtonSetting packet;

    private int offGroundTicks = 0;
    private boolean active = false;

    public HypixelTestFly(String name, @NotNull Fly parent) {
        super(name, parent);
        this.registerSetting(speed = new SliderSetting("Speed", 0.1, 0.01, 0.5, 0.01));
        this.registerSetting(packet = new ButtonSetting("Packet", false));
    }

    @EventListener
    public void onPreMotion(PreMotionEvent event) {
        if (!Utils.nullCheck() || mc.thePlayer.ticksExisted < 20) return;
        if (mc.thePlayer.onGround) {
            if (!Utils.jumpDown())
                mc.thePlayer.jump();
        } else if (offGroundTicks >= 9) {
            if (offGroundTicks % 2 == 0) {
                if (packet.isToggled())
                    PacketUtils.sendPacket(new C03PacketPlayer(true));
                event.setPosZ(event.getPosZ() + Utils.randomizeDouble(0.09, 0.12));  // 0.095
            }
            event.setPosY(event.getPosY() + speed.getInput());
            mc.thePlayer.setPosition(mc.thePlayer.posX, event.getPosY(), mc.thePlayer.posZ);

            mc.thePlayer.motionY = 0.0;
            MoveUtil.strafe(MoveUtil.isMoving() && active ? speed.getInput() / 10 : 0);
        }
    }

    @EventListener
    public void onMoveInput(MoveInputEvent event) {
        if (!active)
            event.cancel();
    }

    @EventListener
    public void onReceivePacket(ReceivePacketEvent event) {
        if (!active && event.getPacket() instanceof S08PacketPlayerPosLook) {
            active = true;
        }
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer.onGround)
            offGroundTicks = 0;
        else
            offGroundTicks++;

        if (active)
            Utils.getTimer().timerSpeed = 0.3f;
    }

    @EventListener
    public void onWorldChange(WorldChangeEvent event) {
        active = false;
    }

    @Override
    public void onDisable() throws Throwable {
        Utils.resetTimer();
        active = false;
    }
}
