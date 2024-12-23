package keystrokesmod.module.impl.movement.fly;

import keystrokesmod.event.player.ScaffoldPlaceEvent;
import keystrokesmod.event.world.WorldChangeEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.movement.Fly;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SubMode;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import keystrokesmod.Client;
import keystrokesmod.eventbus.annotations.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static keystrokesmod.module.ModuleManager.scaffold;

public class FakeFly extends SubMode<Fly> {
    private static final Set<BlockPos> hiddenPos = new HashSet<>();
    private final ButtonSetting keep;

    public FakeFly(String name, @NotNull Fly parent) {
        super(name, parent);
        this.registerSetting(keep = new ButtonSetting("Keep", true));

        Client.EVENT_BUS.register(new Object() {
            @EventListener
            public void onWorldChange(@NotNull WorldChangeEvent event) {
                hiddenPos.clear();
            }
        });
    }

    public static boolean isHidden(BlockPos pos) {
        return hiddenPos.contains(pos);
    }

    public static boolean hideRotation() {
        return ModuleManager.fly.isEnabled() && ModuleManager.fly.mode.getSubModeValues().get((int) ModuleManager.fly.mode.getInput()) instanceof FakeFly;
    }

    @Override
    public void onEnable() {
        scaffold.setHidden(true);
        scaffold.enable();
    }

    @Override
    public void onDisable() {
        scaffold.disable();
        scaffold.setHidden(false);

        if (!keep.isToggled()) {
            hiddenPos.clear();
        }
    }

    @Override
    public void onUpdate() {
        scaffold.setHidden(true);
        scaffold.enable();
    }

    @EventListener
    public void onPlace(@NotNull ScaffoldPlaceEvent event) {
        MovingObjectPosition hitResult = event.getHitResult();
        hiddenPos.add(hitResult.getBlockPos().offset(hitResult.sideHit));
    }
}
