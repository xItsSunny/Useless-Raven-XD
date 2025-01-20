package keystrokesmod;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Unmodifiable
public final class Const {
    public static final String NAME = "Raven XD";
    public static final String VERSION = "3.0.2";
    public static final List<String> CHANGELOG = Collections.unmodifiableList(Arrays.asList(
            "[+] **Re-Add** Ability to choose Anticheat checks",
            "[+] Add E-Sound Cape",
            "[-] Remove Raven-XD mode from ClientSpoofer"
    ));
}
