package keystrokesmod.module.impl.combat.velocity;

import keystrokesmod.event.player.PreVelocityEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.combat.Velocity;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

public class HypixelVelocity extends SubMode<Velocity> {
    private final SliderSetting horizontal;
    private final SliderSetting vertical;
    private final SliderSetting chance;
    private final ButtonSetting cancelAir;
    private final ButtonSetting damageBoost;
    private final ButtonSetting damageBoostOnlyOnGround;
    private final ButtonSetting onlyFirstHit;
    private final SliderSetting resetTime;

    private long lastVelocityTime = 0;

    public HypixelVelocity(String name, @NotNull Velocity parent) {
        super(name, parent);
        this.registerSetting(horizontal = new SliderSetting("Horizontal", 90.0, -100.0, 100.0, 1.0));
        this.registerSetting(vertical = new SliderSetting("Vertical", 100.0, 0.0, 100.0, 1.0));
        this.registerSetting(chance = new SliderSetting("Chance", 100, 0, 100, 1, "%"));
        this.registerSetting(cancelAir = new ButtonSetting("Cancel air", false));
        this.registerSetting(damageBoost = new ButtonSetting("Damage boost", false));
        this.registerSetting(damageBoostOnlyOnGround = new ButtonSetting("Damage boost only on ground", false, damageBoost::isToggled));
        this.registerSetting(onlyFirstHit = new ButtonSetting("Only first hit", false));
        this.registerSetting(resetTime = new SliderSetting("Reset time", 5000, 500, 10000, 500, "ms", onlyFirstHit::isToggled));
    }

    @EventListener
    public void onPreVelocity(@NotNull PreVelocityEvent event) {
        if (ModuleManager.longJump.isEnabled()) return;

        final long time = System.currentTimeMillis();

        if (onlyFirstHit.isToggled() && time - lastVelocityTime < resetTime.getInput())
            return;
        lastVelocityTime = time;

        event.cancel();

        if (!mc.thePlayer.onGround && cancelAir.isToggled()) {
            return;
        }

        double motionX = event.getMotionX() / 8000.0;
        double motionY = event.getMotionY() / 8000.0;
        double motionZ = event.getMotionZ() / 8000.0;

        if (chance.getInput() == 100 || Math.random() * 100 <= chance.getInput()) {
            motionX *= horizontal.getInput() / 100;
            motionY *= vertical.getInput() / 100;
            motionZ *= horizontal.getInput() / 100;
        }

        mc.thePlayer.motionY = choose(mc.thePlayer.motionY, motionY);

        if (damageBoost.isToggled() && !(!mc.thePlayer.onGround && damageBoostOnlyOnGround.isToggled())) {
            MoveUtil.moveFlying(0.2);
        }

        mc.thePlayer.motionX = choose(mc.thePlayer.motionX, motionX);
        mc.thePlayer.motionZ = choose(mc.thePlayer.motionZ, motionZ);
    }

    private double choose(double curMotion, double packetMotion) {
        if (packetMotion == 0)
            return curMotion;
        return packetMotion;
    }
}
