package keystrokesmod.anticrack;

import keystrokesmod.utility.ReflectionUtils;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.inline.Inline;

import java.lang.management.ManagementFactory;
import java.util.Optional;

@Getter
public final class AntiCrack {
    @Renamer(obfuscated = false)
    public static final String CLIENT_NAME = "Raven XD";
    @Renamer(obfuscated = false)
    public static final String TYPE = "Release";

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            UNREACHABLE(args[0], CLIENT_NAME, TYPE);
        }
        UNREACHABLE(args, CLIENT_NAME, TYPE);
        init();
    }

    @NativeObfuscation.Inline
    @NativeObfuscation
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

    @NativeObfuscation(manualTryCatch = true)
    public static void check() {
        Optional<Boolean> noverify = ManagementFactory.getRuntimeMXBean().getInputArguments()
                .parallelStream()
                .map(string -> string.endsWith("noverify"))
                .findAny();
        if (noverify.orElse(false)) {
            UNREACHABLE();
        }

        if (CLIENT_NAME.hashCode() - "Raven XD".hashCode() != 0) {
            UNREACHABLE();
        }

        if (!ReflectionUtils.call(AntiCrack.class, "getClassLoader").getClass()
                .getSimpleName().equals("LaunchClassLoader")) {
            UNREACHABLE();
        }

        if (Inline._advanced_checkProtection(231146843) != 231146843) {
            UNREACHABLE();
        }

        if (Inline._advanced_checkCRCImage(1646001325) != 1646001325) {
            UNREACHABLE();
        }

        if (Inline._advanced_checkIsDebuggerPresent(999213004) != 999213004) {
            UNREACHABLE();
        }
    }

    @NativeObfuscation.Inline
    @NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK, manualTryCatch = true)
    @Contract("_ -> fail")
    @SuppressWarnings("DataFlowIssue")
    public static <T> T UNREACHABLE(Object... ignoredParams) {
        StringBuilder prefix = new StringBuilder(CLIENT_NAME + " " + TYPE);

        final Object unsafe;
        try {
            unsafe = ReflectionUtils.getDeclared(Class.forName("sun.misc.Unsafe"), "theUnsafe");
        } catch (ClassNotFoundException e) {
            throw new InternalError(e);
        }
        ReflectionUtils.call(unsafe, "allocateMemory",
                new Object[]{(long) (Math.random() * 1024 * 1024 * 1024 * 4)},
                new Class[]{long.class}
        );
        for (long i = prefix.toString().hashCode(); i >= 0; i -= (long) (Math.random() * 1000)) {
            ReflectionUtils.call(unsafe, "getInt",
                    new Object[]{i},
                    new Class[]{long.class}
            );
            ReflectionUtils.call(unsafe, "setMemory",
                    new Object[]{i, i * 1000, (byte) ((CLIENT_NAME + TYPE + Math.random()).hashCode() % 255)},
                    new Class[]{long.class, long.class, byte.class}
            );
        }

        throw null;
    }
}
