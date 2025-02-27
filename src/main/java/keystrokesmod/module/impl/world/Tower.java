package keystrokesmod.module.impl.world;

import keystrokesmod.event.client.PreTickEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.world.tower.*;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeValue;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import keystrokesmod.Client;
import keystrokesmod.eventbus.annotations.EventListener;

import static keystrokesmod.module.ModuleManager.scaffold;

public class Tower extends Module {
    private final ButtonSetting disableWhileCollided;
    private final ButtonSetting disableWhileHurt;
    private final ButtonSetting stopMotion;

    private boolean lastTowering = false;

    public Tower() {
        super("Tower", category.world);
        this.registerSetting(new DescriptionSetting("Works with SafeWalk & Scaffold"));
        final ModeValue mode;
        this.registerSetting(mode = new ModeValue("Mode", this)
                .add(new VanillaTower("Vanilla", this))
                .add(new JumpSprintTower("JumpSprint", this))
                .add(new HypixelTower("Hypixel", this))
                .add(new BlocksMCTower("BlocksMC", this))
                .add(new ConstantMotionTower("ConstantMotion", this))
                .add(new VulcanTower("Vulcan", this))
        );

        this.registerSetting(disableWhileCollided = new ButtonSetting("Disable while collided", false));
        this.registerSetting(disableWhileHurt = new ButtonSetting("Disable while hurt", false));
        this.registerSetting(stopMotion = new ButtonSetting("Stop motion", false));
        this.canBeEnabled = false;

        mode.enable();
        Client.EVENT_BUS.register(new Object() {
            @EventListener
            public void onUpdate(PreTickEvent event) {
                if (!Utils.nullCheck()) return;
                final boolean curCanTower = canTower();
                if (!curCanTower && lastTowering && stopMotion.isToggled())
                    MoveUtil.stop();
                lastTowering = curCanTower;
            }
        });
    }

    public boolean canTower() {
        if (scaffold.totalBlocks() == 0) return false;
        if (mc.currentScreen != null) return false;
        if (!Utils.nullCheck() || !Utils.jumpDown()) {
            return false;
        } else if (disableWhileHurt.isToggled() && mc.thePlayer.hurtTime >= 9) {
            return false;
        } else if (disableWhileCollided.isToggled() && mc.thePlayer.isCollidedHorizontally) {
            return false;
        } else return modulesEnabled();
    }

    public boolean modulesEnabled() {
        return ((ModuleManager.safeWalk.isEnabled() && ModuleManager.safeWalk.tower.isToggled() && SafeWalk.canSafeWalk()) || (scaffold.isEnabled() && scaffold.tower.isToggled()));
    }
}
