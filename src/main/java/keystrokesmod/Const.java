package keystrokesmod;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Unmodifiable
public final class Const {
    public static final String NAME = "Raven XD";
    public static final String VERSION = "Developing build";
    public static final List<String> CHANGELOG = Collections.unmodifiableList(Arrays.asList(
            // Add
            "[+] **Add** Optimize.",
            "[+] **Add** 'Hide rotation' to RotationHandler.",
            "[+] **Add** SelfDamage.",
            "    - modes: Fake, GrimAC, Vanilla",
            "[+] **Add** 'Hypixel' mode to Scaffold rotation.",
            // Recode
            "[=] **Recode** event system.",
            "[=] **Recode** HUD.",
            "[=] **Recode** MoveableManager (HUD, TargetHUD, Watermark).",
            "    - now, you can drag them in chat screen.",
            "[=] **Recode** Sprint.",
            "[=] **Recode** rotation system (KillAura & Scaffold).",
            "    - Min/Max rotation speed.",
            "    - Min/Max rotation accuracy (test).",
            "    - Improved GCD.",
            "    - Fix rotation calc order (sometimes attack before rotate).",
            "    - Remove some shits.",
            "[=] **Recode** InvManager (fixed bugs).",
            "[=] **Recode** Trajectories.",
            "    - Better performance. (de-sync compute)",
            "[=] **Recode** TargetStrafe.",
            "[=] **Recode** MainMenu.",
            "    - Hover on client details to show changelog.",
            // Improve
            "[=] **Improve** AutoGapple (works in QuickMacro, but still bad).",
            "[=] **Improve** profile system (improved autosave and autoload).",
            "[=] **Improve** Hypixel Step (less flag).",
            "[=] **Improve** 'Ground' mode in Hypixel Speed.",
            "[=] **Improve** scaffold block search.",
            "[=] **Improve** Yeet (bypass global exception handler).",
            "[=] **Improve** all 'jump' call (fix double jump).",
            "[=] **Improve** Performance.",
            // Fix
            "[=] **Fix** background render ratio on Android device.",
            "[=] **Fix** AutoClicker doesn't work and hit reg.",
            "[=] **Fix** sprint issue in some modules like Scaffold.",
            "    - even can't fix sprint, our game got flaws.",
            "    - hacking never stop, breaking all the laws.",
            "[=] **Fix** 'Telly' schedule in Scaffold (still some bugs).",
            "[=] **Fix** 'Constant' rotation in Scaffold.",
            "[=] **Fix** single player broken.",
            "[=] **Fix** some internal bugs.",
            // Remove
            "[-] **Remove** ArrayList.",
            "[-] **Remove** dynamic system.",
            "[-] **Remove** NoteBot.",
            "[-] **Remove** 'HypixelJump3' from Scaffold sprint (i gave up).",
            "[-] **Remove** splash progress.",
            "[-] **Remove** polar expend scaffold (patched rn).",
            "[-] **Remove** 'Test' mode from Fly.",
            "[-] **Remove** some backgrounds to balance mod jar size."
    ));
}
