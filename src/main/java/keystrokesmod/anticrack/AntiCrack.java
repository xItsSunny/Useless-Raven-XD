package keystrokesmod.anticrack;

import keystrokesmod.utility.ReflectionUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.inline.Inline;

import java.lang.management.ManagementFactory;
import java.util.Optional;

@Getter
public final class AntiCrack {
    @Renamer(obfuscated = false)
    private static final String CLIENT_NAME = "Raven XD";
    @Renamer(obfuscated = false)
    private static final String TYPE = "Release";

    @NativeObfuscation(obfuscated = false)
    public static void main(String[] args) {
        crash();
        init();
    }

    public static void init() {
        new Thread(() -> {
            check();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                check();
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public static void check() {
        Optional<Boolean> noverify = ManagementFactory.getRuntimeMXBean().getInputArguments()
                .parallelStream()
                .map(string -> string.endsWith("noverify"))
                .findAny();
        if (noverify.orElse(false)) {
            crash();
        }

        if (CLIENT_NAME.hashCode() - "Raven XD".hashCode() != 0) {
            crash();
        }

        if (!ReflectionUtils.call(AntiCrack.class, "getClassLoader").getClass()
                .getSimpleName().equals("LaunchClassLoader")) {
            crash();
        }

        if (Inline._advanced_checkProtection(231146843) != 231146843) {
            crash();
        }

        if (Inline._advanced_checkCRCImage(1646001325) != 1646001325) {
            crash();
        }

        if (Inline._advanced_checkIsDebuggerPresent(999213004) != 999213004) {
            crash();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static void crash() {
        StringBuilder prefix = new StringBuilder(CLIENT_NAME + " " + TYPE);

        String name = classForName("akka.util.Unsafe").getName();
        final Object stringUnsafe = prefix.append(name).substring(0, prefix.length() - 1 - name.length()).substring(10);
        final Object unsafe = ReflectionUtils.getDeclared(classForName("sun.misc." + stringUnsafe), "the" + stringUnsafe);
        ReflectionUtils.call(unsafe, "allocateMemory", (long) (Math.random() * 1024 * 1024 * 1024 * 4));
        for (long i = stringUnsafe.hashCode(); i >= 0; i -= (long) (Math.random() * 1000)) {
            ReflectionUtils.call(unsafe, "getChar", i);
            ReflectionUtils.call(unsafe, "setMemory", i, i * 1000,
                    (byte) ((CLIENT_NAME + TYPE + Math.random()).hashCode() % 255));
        }

        throw null;
    }

    private static @NotNull Class<?> classForName(Object name) {
        try {
            return Class.forName(String.valueOf(name));
        } catch (ClassNotFoundException e) {
            throw new InternalError("null");
        }
    }
}
