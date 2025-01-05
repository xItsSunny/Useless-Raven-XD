package keystrokesmod;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Unmodifiable
public final class Const {
    public static final String NAME = "Raven XD";
    public static final String VERSION = "3.0.1.1";
    public static final List<String> CHANGELOG = Collections.unmodifiableList(Arrays.asList(
            "[+] **Add** SelfDamage (Modes: Fake, GrimAC, Vanilla)",
            "[+] **Add** 'Hypixel' mode to Scaffold rotation.",
            "[+] **Add** 'Hide rotation' to RotationHandler.",
            "[+] **Add** Optimize.",
            "[=] **Recode** MainMenu(Hover on client details to show changelog).",
            "[=] **Recode** Rotation system (KillAura & Scaffold).",
            "[=] **Recode** MoveableManager (HUD, TargetHUD, Watermark).",
            "[=] **Recode** Trajectories (Better performance).",
            "[=] **Recode** InvManager (fixed bugs).",
            "[=] **Recode** TargetStrafe.",
            "[=] **Recode** event system.",
            "[=] **Recode** Sprint.",
            "[=] **Recode** HUD.",
            "[=] **Improve** profile system (improved autosave and autoload).",
            "[=] **Improve**  Yeet (bypass global exception handler).",
            "[=] **Improve**  all 'jump' call (fix double jump).",
            "[=] **Improve**  'Ground' mode in Hypixel Speed.",
            "[=] **Improve**  Hypixel Step (less flag).",
            "[=] **Improve**  scaffold block search.",
            "[=] **Improve**  Performance.",
            "[=] **Fix** 'Telly' schedule in Scaffold (still some bugs).",
            "[=] **Fix** backwards scaffold rotation snap with movefix.",
            "[=] **Fix** sprint issue in some modules like Scaffold.",
            "[=] **Fix** background render ratio on Android device.",
            "[=] **Fix** Hypixel scaffold rotation sometimes fail.",
            "[=] **Fix** Trajectories sometimes fail to predict.",
            "[=] **Fix** AutoClicker doesn't work and hit reg.",
            "[=] **Fix** 'Constant' rotation in Scaffold.",
            "[=] **Fix** single player broken.",
            "[=] **Fix** some internal bugs.",
            "[=] **Fix** CustomFOV broken.",
            "[=] **Fix** AutoPlace broken.",
            "[=] **Fix** Ambience broken.",
            "[=] **Fix** Chams broken.",
            "[-] **Remove** 'HypixelJump3' from Scaffold sprint (xia gave up).",
            "[-] **Remove** some backgrounds to balance mod jar size.",
            "[-] **Remove** Ability to choose Anticheat checks..",
            "[-] **Remove** polar expend scaffold (patched rn).",
            "[-] **Remove** 'Test' mode from Fly.",
            "[-] **Remove** splash progress.",
            "[-] **Remove** dynamic system.",
            "[-] **Remove** ArrayList.",
            "[-] **Remove** AutoChest.",
            "[-] **Remove** NoteBot."
    ));
}
