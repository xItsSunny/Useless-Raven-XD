package keystrokesmod.module.impl.movement.noslow;

import keystrokesmod.event.player.PreMotionEvent;
import keystrokesmod.event.network.SendPacketEvent;
import keystrokesmod.module.impl.movement.NoSlow;
import keystrokesmod.module.impl.other.SlotHandler;
import keystrokesmod.module.impl.player.blink.NormalBlink;
import keystrokesmod.utility.ContainerUtils;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.PacketUtils;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.EnumFacing.DOWN;

public class HypixelNoSlow extends INoSlow {
    private int offGroundTicks = 0;
    private boolean send = false;

    private final NormalBlink blink = new NormalBlink("Blink", this);
    private boolean cycle = false;

    public HypixelNoSlow(String name, @NotNull NoSlow parent) {
        super(name, parent);
    }

    @EventListener
    public void onPreMotion(PreMotionEvent event) {
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;
        } else {
            offGroundTicks++;
        }

        final @Nullable ItemStack item = SlotHandler.getHeldItem();
        if (offGroundTicks == 4 && send) {
            send = false;
            if (mc.thePlayer.isUsingItem()) {
                PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(
                        new BlockPos(-1, -1, -1),
                        255, item,
                        0, 0, 0
                ));
            }

        } else if (item != null && mc.thePlayer.isUsingItem() && !(item.getItem() instanceof ItemSword)) {
            event.setPosY(event.getPosY() + 1E-14);
        }

        if (NoSlow.sword.isToggled())
            if (mc.thePlayer.isUsingItem()
                    && item != null && item.getItem() instanceof ItemSword) {
                if (cycle) {
                    blink.enable();
                    PacketUtils.sendPacket(new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, DOWN
                    ));
                    cycle = false;
                } else {
                    blink.disable();
                    PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(SlotHandler.getHeldItem()));
                    cycle = true;
                }
            } else {
                cycle = false;
                blink.disable();
            }
    }

    @Override
    public void onDisable() throws Throwable {
        cycle = false;
        blink.disable();
    }

    @EventListener
    public void onSendPacket(@NotNull SendPacketEvent event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement && !mc.thePlayer.isUsingItem()) {
            C08PacketPlayerBlockPlacement blockPlacement = (C08PacketPlayerBlockPlacement) event.getPacket();
            if (SlotHandler.getHeldItem() != null && blockPlacement.getPlacedBlockDirection() == 255
                    && (ContainerUtils.isRest(SlotHandler.getHeldItem().getItem()) || SlotHandler.getHeldItem().getItem() instanceof ItemBow || SlotHandler.getHeldItem().getItem() instanceof ItemPotion) && offGroundTicks < 2) {
                if (mc.thePlayer.onGround) {
                    MoveUtil.jump();
                }
                send = true;
                event.cancel();
            }
        } else if (event.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging packet = (C07PacketPlayerDigging) event.getPacket();
            if (packet.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                if (send) {
                    // or get bad packet flag
                    event.cancel();
                }
                send = false;
            }
        }
    }

    @Override
    public float getSlowdown() {
        return 1;
    }
}
