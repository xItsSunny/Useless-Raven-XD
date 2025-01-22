package keystrokesmod.module.impl.other;

import keystrokesmod.Client;
import keystrokesmod.event.player.PreMotionEvent;
import keystrokesmod.event.world.WorldChangeEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.LiteralSubMode;
import keystrokesmod.module.setting.impl.ModeValue;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import keystrokesmod.eventbus.annotations.EventListener;

import java.util.concurrent.TimeUnit;

public class AutoGG extends Module {
    public static String customGG = null;
    private final ModeValue mode;
    private static SliderSetting minDelay;
    private static SliderSetting maxDelay;
    private boolean active;
    private boolean worldChanged;

    public AutoGG() {
        super("AutoGG", Module.category.other);
        this.registerSetting(mode = new ModeValue("Mode", this)
                .add(new LiteralSubMode("Normal", this))
                .add(new LiteralSubMode("RavenXD", this))
                .add(new LiteralSubMode("Myau", this))
                .add(new LiteralSubMode("Custom", this))
        );

        this.registerSetting(minDelay = new SliderSetting("Min Delay", 0, 0, 5000, 100, "ms"));
        this.registerSetting(maxDelay = new SliderSetting("Max Delay", 1000, 0, 5000, 100, "ms"));
    }

    @Override
    public void onEnable() {
        worldChanged = true;
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
    }

    @EventListener
    public void onPreMotionEvent(PreMotionEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.thePlayer.ticksExisted % 18 != 0 || mc.isIntegratedServerRunning()) {
            return;
        }

        long visiblePlayers = mc.theWorld.playerEntities.stream()
                .filter(player -> !player.isInvisible() || player == mc.thePlayer)
                .count();

        if (visiblePlayers <= 1) {
            if (active) {
                mc.thePlayer.sendChatMessage(getMessageForMode());
                active = false;

                long delay = Utils.randomizeInt(minDelay.getInput(), maxDelay.getInput());

                Client.getExecutor().schedule(() -> {
                    mc.thePlayer.sendChatMessage(getMessageForMode());
                }, delay, TimeUnit.MILLISECONDS);
            }
        } else if (worldChanged) {
            active = true;
            worldChanged = false;
        }
    }

    @EventListener
    public void onWorldChange(WorldChangeEvent event) {
        worldChanged = true;
    }

    private String getMessageForMode() {
        switch (mode.getSelected().getPrettyName()) {
            case "RavenXD":
                return "GGs xD";
            case "Myau":
                return "gg gf ^_^";
            case "Custom":
                return customGG;
            default: // "Normal"
                return "GG";
        }
    }
}
