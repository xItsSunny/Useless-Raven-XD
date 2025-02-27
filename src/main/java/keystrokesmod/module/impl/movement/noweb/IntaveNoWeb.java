package keystrokesmod.module.impl.movement.noweb;

import keystrokesmod.event.world.BlockWebEvent;
import keystrokesmod.event.player.MoveInputEvent;
import keystrokesmod.event.player.PreUpdateEvent;
import keystrokesmod.module.impl.movement.NoWeb;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

public class IntaveNoWeb extends SubMode<NoWeb> {
    private final ButtonSetting groundBoost;
    private final ButtonSetting noDown;
    private final ButtonSetting upAndDown;

    private BlockPos lastWeb = null;
    private boolean webbing = false;

    public IntaveNoWeb(String name, @NotNull NoWeb parent) {
        super(name, parent);
        this.registerSetting(groundBoost = new ButtonSetting("Ground boost", false));
        this.registerSetting(noDown = new ButtonSetting("No down", false));
        this.registerSetting(upAndDown = new ButtonSetting("UpAndDown", false, noDown::isToggled));
    }

    @EventListener
    public void onWeb(@NotNull BlockWebEvent event) {
        lastWeb = event.getBlockPos();
    }

    @EventListener(priority = 1)
    public void onPreUpdate(PreUpdateEvent event) {
        if (lastWeb == null || !Utils.nullCheck() || BlockUtils.getBlock(lastWeb) != Blocks.web) {
            if (webbing)
                Utils.resetTimer();
            webbing = false;
            lastWeb = null;
            return;
        }

        AxisAlignedBB box = new AxisAlignedBB(lastWeb, lastWeb.add(1, 1, 1));
        if (box.intersectsWith(mc.thePlayer.getEntityBoundingBox())) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = MoveUtil.jumpMotion();
                MoveUtil.moveFlying(groundBoost.isToggled() ? 0.4 : 0.3);
            } else if (noDown.isToggled()) {
                if (upAndDown.isToggled())
                    if (mc.gameSettings.keyBindSneak.isKeyDown())
                        mc.thePlayer.motionY = -0.2;
                    else if (mc.gameSettings.keyBindJump.isKeyDown())
                        mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 2 == 0 ? 0.2 : -0.01;
                    else
                        mc.thePlayer.motionY = -0.01;
                else
                    mc.thePlayer.motionY = -0.01;
            }

            webbing = true;
        } else {
            if (webbing) {
                webbing = false;
            }
            lastWeb = null;
        }
    }

    @Override
    public void onDisable() {
        webbing = false;
        lastWeb = null;
    }

    @EventListener
    public void onMove(MoveInputEvent event) {
        if (webbing)
            event.setJump(false);
    }
}
