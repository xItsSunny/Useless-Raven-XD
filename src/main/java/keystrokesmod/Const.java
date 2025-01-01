package keystrokesmod;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Unmodifiable
public final class Const {
    public static final String NAME = "Raven XD";
    public static final String VERSION = "3.0.1";
    public static final List<String> CHANGELOG = Collections.unmodifiableList(Arrays.asList(
            "[=] **Fix** backwards scaffold rotation snap with movefix.",
            "[=] **Fix** Hypixel scaffold rotation sometimes fail.",
            "[=] **Fix** Trajectories sometimes fail to predict.",
            "[=] **Fix** CustomFOV broken.",
            "[=] **Fix** AutoPlace broken.",
            "[=] **Fix** Ambience broken.",
            "[=] **Fix** Chams broken.",
            "[-] **Remove** AutoChest."
    ));
}
