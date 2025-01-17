package keystrokesmod.module.impl.combat.ragebot.rapidfire;

import keystrokesmod.event.player.PreMotionEvent;
import keystrokesmod.event.client.PreTickEvent;
import keystrokesmod.event.network.ReceivePacketEvent;
import keystrokesmod.module.impl.combat.RageBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.backtrack.TimedPacket;
import keystrokesmod.utility.render.Animation;
import keystrokesmod.utility.render.Easing;
import keystrokesmod.utility.render.progress.Progress;
import keystrokesmod.utility.render.progress.ProgressManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import keystrokesmod.eventbus.annotations.EventListener;
import keystrokesmod.event.render.Render2DEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StoreRapidFire extends LegitRapidFire {
    private static final ButtonSetting disableAntiAim = new ButtonSetting("Disable antiAim", true);
    private static boolean storing = false;
    private final SliderSetting ticks;
    private final SliderSetting maxStoreTime;
    private final ButtonSetting visual;
    private final Queue<TimedPacket> packetQueue = new ConcurrentLinkedQueue<>();
    private final List<Packet<?>> skipPackets = new ArrayList<>();
    private final Animation animation = new Animation(Easing.EASE_OUT_CIRC, 200);
    private final Progress progress = new Progress("Rapid fire");
    private int storeTicks = 0;
    private boolean fire;
    private long lastEndStoreTime = 0;

    public StoreRapidFire(String name, @NotNull RageBot parent) {
        super(name, parent);
        this.registerSetting(ticks = new SliderSetting("Ticks", 4, 1, 10, 1));
        this.registerSetting(maxStoreTime = new SliderSetting("Max store time", 3000, 1000, 30000, 1000, "ms"));
        this.registerSetting(visual = new ButtonSetting("Visual", true));
        this.registerSetting(disableAntiAim);
        progress.registerPreRender(() -> {
            animation.run(storeTicks);
            progress.setProgress(Utils.limit(animation.getValue() / ticks.getInput(), 0, 1));
        });
    }

    @Override
    public void onFire() {
        fire = true;
    }

    @EventListener
    public void onRender(Render2DEvent event) {
        if (!Utils.nullCheck() || !fire || storing) return;

        synchronized (packetQueue) {
            for (int i = 0; i < storeTicks; i++) {
                mc.thePlayer.onUpdate();
            }
            releaseAll();
        }

        fire = false;
    }

    @EventListener(priority = -2)
    public void onPreMotion(PreMotionEvent event) {
        if (canStore()) {
            event.cancel();
            storing = true;
            storeTicks = (int) Math.min(storeTicks + 1, ticks.getInput());
            progress.setText("Rapid fire " + storeTicks);
            lastEndStoreTime = System.currentTimeMillis();
        } else {
            storing = false;
            if (storeTicks > 0 && System.currentTimeMillis() - lastEndStoreTime > maxStoreTime.getInput())
                releaseAll();
        }

        if (storeTicks > 0 && visual.isToggled()) {
            ProgressManager.add(progress);
        } else {
            ProgressManager.remove(progress);
        }
    }

    @EventListener
    public void onReceivePacket(@NotNull ReceivePacketEvent event) {
        if (!Utils.nullCheck()) return;
        Packet<?> p = event.getPacket();
        if (skipPackets.contains(p)) {
            skipPackets.remove(p);
            return;
        }

        try {
            if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 20) {
                packetQueue.clear();
                return;
            }

            if (event.isCancelled())
                return;

            if (p instanceof S08PacketPlayerPosLook || p instanceof S40PacketDisconnect) {
                releaseAll();
                return;
            }

            if (!(p instanceof S32PacketConfirmTransaction || p instanceof S00PacketKeepAlive || p instanceof S12PacketEntityVelocity))
                return;

            packetQueue.add(new TimedPacket(p));
            event.cancel();
        } catch (NullPointerException ignored) {

        }
    }

    @EventListener
    public void onPreTick(PreTickEvent e) {
        synchronized (packetQueue) {
            while (!packetQueue.isEmpty()) {
                try {
                    if (!storing && packetQueue.element().getCold().getCum((long) ticks.getInput() * 50)) {
                        Packet<?> packet = packetQueue.remove().getPacket();
                        skipPackets.add(packet);
                        PacketUtils.receivePacket(packet);
                    } else {
                        return;
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }
    }

    private boolean canStore() {
        if (MoveUtil.isMoving() || MoveUtil.isRealMoving())
            return false;
        if (!mc.thePlayer.onGround)
            return false;
        return !parent.targeted;
    }

    private void releaseAll() {
        storeTicks = 0;
        if (!packetQueue.isEmpty()) {
            for (TimedPacket timedPacket : packetQueue) {
                Packet<?> packet = timedPacket.getPacket();
                skipPackets.add(packet);
                PacketUtils.receivePacket(packet);
            }
            packetQueue.clear();
        }
    }

    @Override
    public void onDisable() throws Throwable {
        storing = false;
        releaseAll();

        animation.setValue(0);
        ProgressManager.remove(progress);
    }
}
