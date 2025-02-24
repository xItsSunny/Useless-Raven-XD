package keystrokesmod.module.impl.combat.velocity;

import keystrokesmod.event.player.PostVelocityEvent;
import keystrokesmod.module.impl.combat.Velocity;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import keystrokesmod.event.network.AttackEntityEvent;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class IntaveVelocity extends SubMode<Velocity> {
    private final SliderSetting xzOnHit;
    private final SliderSetting xzOnSprintHit;
    private final ButtonSetting reduceUnnecessarySlowdown;
    private final SliderSetting chance;
    private final ButtonSetting jump;
    private final ButtonSetting jumpInInv;
    private final SliderSetting jumpChance;
    private final ButtonSetting notWhileSpeed;
    private final ButtonSetting notWhileJumpBoost;
    private final ButtonSetting debug;

    private boolean reduced = false;

    public IntaveVelocity(String name, @NotNull Velocity parent) {
        super(name, parent);
        this.registerSetting(xzOnHit = new SliderSetting("XZ on hit", 0.6, 0, 1, 0.01));
        this.registerSetting(xzOnSprintHit = new SliderSetting("XZ on sprint hit", 0.6, 0, 1, 0.01));
        this.registerSetting(reduceUnnecessarySlowdown = new ButtonSetting("Reduce unnecessary slowdown", false));
        this.registerSetting(chance = new SliderSetting("Chance", 100, 0, 100, 1, "%"));
        this.registerSetting(jump = new ButtonSetting("Jump", false));
        this.registerSetting(jumpInInv = new ButtonSetting("Jump in inv", false, jump::isToggled));
        this.registerSetting(jumpChance = new SliderSetting("Jump chance", 80, 0, 100, 1, "%", jump::isToggled));
        this.registerSetting(notWhileSpeed = new ButtonSetting("Not while speed", false));
        this.registerSetting(notWhileJumpBoost = new ButtonSetting("Not while jump boost", false));
        this.registerSetting(debug = new ButtonSetting("Debug", false));
    }

    @Override
    public void onEnable() {
        reduced = false;
    }

    @EventListener
    public void onPostVelocity(PostVelocityEvent event) {
        if (noAction()) return;

        if (jump.isToggled()) {
            if (ThreadLocalRandom.current().nextDouble() > jumpChance.getInput() / 100) return;

            if (mc.thePlayer.onGround && (jumpInInv.isToggled() || mc.currentScreen == null))
                MoveUtil.jump();
        }
    }

    @EventListener
    public void onAttack(@NotNull AttackEntityEvent event) {
        if (!(event.getTarget() instanceof EntityLivingBase) || mc.thePlayer.hurtTime <= 0) return;
        if (noAction() || ThreadLocalRandom.current().nextDouble() > chance.getInput() / 100) return;
        if (reduceUnnecessarySlowdown.isToggled() && reduced) return;

        double reduction = mc.thePlayer.isSprinting() ? xzOnSprintHit.getInput() : xzOnHit.getInput();
        
        mc.thePlayer.motionX *= reduction;
        mc.thePlayer.motionZ *= reduction;
        mc.thePlayer.motionY *= 0.98;
        
        reduced = true;

        if (debug.isToggled()) {
            Utils.sendMessage(String.format("Velocity reduced: XZ=%.3f Y=%.3f", mc.thePlayer.motionX, mc.thePlayer.motionY));
        }
    }

    private boolean noAction() {
        return (notWhileSpeed.isToggled() && mc.thePlayer.isPotionActive(Potion.moveSpeed)) ||
               (notWhileJumpBoost.isToggled() && mc.thePlayer.isPotionActive(Potion.jump));
    }
}
