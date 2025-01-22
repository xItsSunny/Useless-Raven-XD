package keystrokesmod.module.impl.other;

import keystrokesmod.Client;
import keystrokesmod.eventbus.annotations.EventListener;
import keystrokesmod.event.player.PreMotionEvent;
import keystrokesmod.event.world.WorldChangeEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.LiteralSubMode;
import keystrokesmod.module.setting.impl.ModeValue;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;

import java.util.concurrent.TimeUnit;

public class Spammer extends Module {

    private final ModeValue mode;
    private final SliderSetting minDelay;
    private final SliderSetting maxDelay;

    private boolean active;

    public Spammer() {
        super("Spammer", category.other);

        this.registerSetting(mode = new ModeValue("Mode", this)
                .add(new LiteralSubMode("Waste", this))
                .add(new LiteralSubMode("RIPRavenXD", this))
                .add(new LiteralSubMode("StartCheating", this))
        );

        this.registerSetting(minDelay = new SliderSetting("Min Delay", 0, 0, 30000, 100, "ms"));
        this.registerSetting(maxDelay = new SliderSetting("Max Delay", 1000, 0, 30000, 100, "ms"));
    }

    @Override
    public void onEnable() {
        active = true;
        startSpamming();
    }

    @Override
    public void onDisable() {
        active = false;
    }

    @EventListener
    public void onPreMotionEvent(PreMotionEvent event) {
    }

    @EventListener
    public void onWorldChange(WorldChangeEvent event) {
    }

    private void startSpamming() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        spamMessageWithDelay();
    }

    private void spamMessageWithDelay() {
        if (!active) return;

        String message = getMessageForMode();

        sendSpammedMessage(message);

        long delay = Utils.randomizeInt(minDelay.getInput(), maxDelay.getInput());

        Client.getExecutor().schedule(() -> {
            spamMessageWithDelay();
        }, delay, TimeUnit.MILLISECONDS);
    }

    private String getMessageForMode() {
        switch (mode.getSelected().getPrettyName()) {
            case "StartCheating":
                return "Start cheating today with Useless Raven XD!";
            case "RIPRavenXD":
                return "Raven-XD might be discontinued but Useless Raven XD is still up!";
            default: // "Waste"
                return "Stop wasting your time and get Useless Raven XD!";
        }
    }

    private void sendSpammedMessage(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage(message);
        }
    }
}
