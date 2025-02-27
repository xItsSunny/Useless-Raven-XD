package keystrokesmod.module.impl.other;

import keystrokesmod.Client;
import keystrokesmod.event.world.WorldChangeEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.other.anticheats.PlayerManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.World;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author xia__mc
 * @see <a href="https://github.com/Nova-Committee/CheatDetector">CheatDetector Github</a>
 */
public class Anticheat extends Module {
    @Getter
    private static SliderSetting latency;
    @Getter
    private static SliderSetting threshold;
    @Getter
    private static ButtonSetting disableInLobby;
    @Getter
    private static ButtonSetting checkForSelf;
    @Getter
    private static ButtonSetting checkForTeammates;
    @Getter
    private static SliderSetting vlClearTime;
    @Getter
    private static ButtonSetting noAlertBuffer;
    @Getter
    private static ButtonSetting shouldPing;
    @Getter
    private static ModeSetting pingSound;
    @Getter
    private static ModeSetting autoReport;


    @Getter
    private static ButtonSetting experimentalMode;


    @Getter
    private static ButtonSetting combatCheck;

    @Getter
    private static ButtonSetting combatCheckAutoBlockA;
    @Getter
    private static ButtonSetting combatCheckAutoClickerA;
    @Getter
    private static ButtonSetting combatCheckNoSlowA;
    @Getter
    private static ButtonSetting combatCheckReachA;


    @Getter
    private static ButtonSetting movementCheck;

    @Getter
    private static ButtonSetting movementCheckBlinkA;
    @Getter
    private static ButtonSetting movementCheckFlyA;
    @Getter
    private static ButtonSetting movementCheckGroundSpoofA;
    @Getter
    private static ButtonSetting movementCheckGroundSpoofB;
    @Getter
    private static ButtonSetting movementCheckMotionA;
    @Getter
    private static ButtonSetting movementCheckNoFallA;
    @Getter
    private static ButtonSetting movementCheckSpeedA;
    @Getter
    private static ButtonSetting movementCheckSpeedB;
    @Getter
    private static ButtonSetting movementCheckSpeedC;


    @Getter
    private static ButtonSetting scaffoldingCheck;

    @Getter
    private static ButtonSetting scaffoldingCheckScaffoldA;
    @Getter
    private static ButtonSetting scaffoldingCheckScaffoldB;
    @Getter
    private static ButtonSetting scaffoldingCheckScaffoldC;


    @Getter
    private static ButtonSetting simulationCheck;

    private PlayerManager manager = new PlayerManager();

    public Anticheat() {
        super("Anticheat", category.other);
        this.registerSetting(new DescriptionSetting("Tries to detect cheaters."));
        this.registerSetting(latency = new SliderSetting("Latency compensation", 600.0, 0.0, 1000.0, 1.0, "ms"));
        this.registerSetting(threshold = new SliderSetting("Movement threshold", 1.0, 0.0, 3.0, 0.01, "blocks"));
        this.registerSetting(disableInLobby = new ButtonSetting("Disable in lobby", true));
        this.registerSetting(checkForSelf = new ButtonSetting("Check for self", true));
        this.registerSetting(checkForTeammates = new ButtonSetting("Check for teammates", true));
        this.registerSetting(vlClearTime = new SliderSetting("VL clear time", 6000, -1, 12000, 1, "ticks"));
        this.registerSetting(noAlertBuffer = new ButtonSetting("Remove alert buffer", false));
        this.registerSetting(shouldPing = new ButtonSetting("Should ping", true));
        this.registerSetting(pingSound = new ModeSetting("Ping sound", new String[]{"Note", "Augustus"}, 0, shouldPing::isToggled));
        this.registerSetting(autoReport = new ModeSetting("Auto report", new String[]{"None", "/wdr", "/report"}, 0));
        this.registerSetting(experimentalMode = new ButtonSetting("Experimental mode", false));


        this.registerSetting(combatCheck = new ButtonSetting("Combat checks", true));

        this.registerSetting(combatCheckAutoBlockA = new ButtonSetting("AutoBlockA", true, combatCheck::isToggled));
        this.registerSetting(combatCheckAutoClickerA = new ButtonSetting("AutoClickerA", true, combatCheck::isToggled));
        this.registerSetting(combatCheckNoSlowA = new ButtonSetting("NoSlowA", true, combatCheck::isToggled));
        this.registerSetting(combatCheckReachA = new ButtonSetting("ReachA", false, () -> (combatCheck.isToggled() && experimentalMode.isToggled())));


        this.registerSetting(movementCheck = new ButtonSetting("Movement checks", true));

        this.registerSetting(movementCheckBlinkA = new ButtonSetting("BlinkA", true, movementCheck::isToggled));
        this.registerSetting(movementCheckFlyA = new ButtonSetting("FlyA", true, movementCheck::isToggled));
        this.registerSetting(movementCheckGroundSpoofA = new ButtonSetting("GroundSpoofA", true, movementCheck::isToggled));
        this.registerSetting(movementCheckGroundSpoofB = new ButtonSetting("GroundSpoofB", true, movementCheck::isToggled));
        this.registerSetting(movementCheckMotionA = new ButtonSetting("MotionA", false, () -> (movementCheck.isToggled() && experimentalMode.isToggled())));
        this.registerSetting(new DescriptionSetting("<!> MotionA is not finished", () -> (movementCheck.isToggled() && movementCheckMotionA.isToggled() && experimentalMode.isToggled())));
        this.registerSetting(movementCheckNoFallA = new ButtonSetting("NoFallA", true, movementCheck::isToggled));
        this.registerSetting(movementCheckSpeedA = new ButtonSetting("SpeedA", true, movementCheck::isToggled));
        this.registerSetting(movementCheckSpeedB = new ButtonSetting("SpeedB", true, movementCheck::isToggled));
        this.registerSetting(movementCheckSpeedC = new ButtonSetting("SpeedC", true, movementCheck::isToggled));


        this.registerSetting(scaffoldingCheck = new ButtonSetting("Scaffolding checks", true));

        this.registerSetting(scaffoldingCheckScaffoldA = new ButtonSetting("ScaffoldA", false, () -> (scaffoldingCheck.isToggled() && experimentalMode.isToggled())));
        this.registerSetting(scaffoldingCheckScaffoldB = new ButtonSetting("ScaffoldB", true, scaffoldingCheck::isToggled));
        this.registerSetting(scaffoldingCheckScaffoldC = new ButtonSetting("ScaffoldC", true, scaffoldingCheck::isToggled));


        this.registerSetting(simulationCheck = new ButtonSetting("Simulation checks", false));

    }

    public void onUpdate() {
        manager.update(Client.mc);
    }

    @SubscribeEvent
    public void onEntityJoin(@NotNull WorldChangeEvent e) {
        manager = null;
        manager = new PlayerManager();
    }

    @Override
    public void onDisable() throws Throwable {
        //noinspection SynchronizeOnNonFinalField
        synchronized (manager) {
            manager.dataMap.values().forEach(trPlayer -> trPlayer.manager.onCustomAction(MinecraftForge.EVENT_BUS::unregister));
            manager = null;
        }
    }

    @Override
    public void onEnable() throws Throwable {
        manager = new PlayerManager();
    }
}
