package keystrokesmod.module.impl.world;

import keystrokesmod.event.player.PreUpdateEvent;
import keystrokesmod.event.player.SafeWalkEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

public class SafeWalk extends Module {
    private static ButtonSetting shift, blocksOnly, pitchCheck, disableOnForward;
    private final SliderSetting shiftDelay;
    private final SliderSetting motion;
    public ButtonSetting tower;
    private boolean isSneaking;
    private long b = 0L;

    public SafeWalk() {
        super("SafeWalk", Module.category.world, 0);
        this.registerSetting(shiftDelay = new SliderSetting("Delay until next shift", 0.0, 0.0, 800.0, 10.0));
        this.registerSetting(motion = new SliderSetting("Motion", 1.0, 0.5, 1.2, 0.01));
        this.registerSetting(blocksOnly = new ButtonSetting("Blocks only", true));
        this.registerSetting(disableOnForward = new ButtonSetting("Disable on forward", false));
        this.registerSetting(pitchCheck = new ButtonSetting("Pitch check", false));
        this.registerSetting(shift = new ButtonSetting("Shift", false));
        this.registerSetting(tower = new ButtonSetting("Tower", false));
    }

    public static boolean canSafeWalk() {
        if (ModuleManager.safeWalk != null && ModuleManager.safeWalk.isEnabled()) {
            if (mc.currentScreen != null)
                return false;
            if (disableOnForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                return false;
            }
            if (pitchCheck.isToggled() && mc.thePlayer.rotationPitch < 70) {
                return false;
            }
            if (blocksOnly.isToggled() && (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void onDisable() {
        if (shift.isToggled() && Utils.overAir()) {
            this.setSneakState(false);
        }
        isSneaking = false;
    }

    public void onUpdate() {
        if (motion.getInput() != 1.0 && mc.thePlayer.onGround && Utils.isMoving() && (!pitchCheck.isToggled() || mc.thePlayer.rotationPitch >= 70.0f)) {
            Utils.setSpeed(Utils.getHorizontalSpeed() * motion.getInput());
        }
    }

    @EventListener
    public void onTick(PreUpdateEvent e) {
        if (!shift.isToggled() || Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) || !Utils.nullCheck()) {
            return;
        }
        if (mc.thePlayer.onGround && Utils.overAir()) {
            if (blocksOnly.isToggled()) {
                final ItemStack getHeldItem = mc.thePlayer.getHeldItem();
                if (getHeldItem == null || !(getHeldItem.getItem() instanceof ItemBlock)) {
                    this.setSneakState(false);
                    return;
                }
            }
            if (disableOnForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                this.setSneakState(false);
                return;
            }
            if (pitchCheck.isToggled() && mc.thePlayer.rotationPitch < 70.0f) {
                this.setSneakState(false);
                return;
            }
            this.setSneakState(true);
        } else if (this.isSneaking) {
            this.setSneakState(false);
        }
        if (this.isSneaking && mc.thePlayer.capabilities.isFlying) {
            this.setSneakState(false);
        }
    }

    @EventListener
    public void onSafeWalk(@NotNull SafeWalkEvent event) {
        if (canSafeWalk())
            event.setSafeWalk(true);
    }

    private void setSneakState(boolean down) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), down);

        if (this.isSneaking) {
            if (down) {
                return;
            }
        } else if (!down) {
            return;
        }
        if (down) {
            final long n = (long) shiftDelay.getInput();
            if (n != 0L) {
                if (Utils.getDifference(this.b, System.currentTimeMillis()) < n) {
                    return;
                }
                this.b = System.currentTimeMillis();
            }
        } else {
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                return;
            }
            this.b = System.currentTimeMillis();
        }
        final int getKeyCode = mc.gameSettings.keyBindSneak.getKeyCode();
        this.isSneaking = down;
        KeyBinding.setKeyBindState(getKeyCode, down);
    }
}
