package com.noctarius.bz2java;

import com.noctarius.bz2java.jnr.BZ_STREAM;
import com.noctarius.bz2java.jnr.LibBz2;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Platform;
import jnr.ffi.Runtime;
import sun.misc.Unsafe;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

final class NativeUtils {

    static final int BUFFER_SIZE = 1024 * 1024;

    static final EnumSet<StandardOpenOption> WRITE_OPEN_OPTIONS = EnumSet
            .of(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

    private static final Platform PLATFORM = Platform.getNativePlatform();

    private static final String LIBRARY_NAME = buildLibraryName();
    private static final LibBz2 LIB_BZ_2 = LibraryLoader.<LibBz2>create(LibBz2.class).load(LIBRARY_NAME);
    private static final jnr.ffi.Runtime RUNTIME = Runtime.getRuntime(LIB_BZ_2);

    private static final Unsafe UNSAFE;

    static {
        UNSAFE = NativeUtils.retrieveUnsafe();
    }

    private NativeUtils() {
    }

    static void handleNativeError(int result) {
        switch (result) {
            case LibBz2.BZ_MEM_ERROR:
                throw new OutOfMemoryError("Out of native memory for compression / decompression");

            case LibBz2.BZ_PARAM_ERROR:
                throw new IllegalArgumentException("Illegal parameter in native code");

            default:
                throw new InternalError("Native error: " + result);
        }
    }

    static long allocateMemory(int bufferSize) {
        return UNSAFE.allocateMemory(bufferSize);
    }

    static void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    static int copyToNative(byte[] array, long address, int length) {
        UNSAFE.copyMemory(array, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, length);
        return length;
    }

    static int copyFromNative(long address, byte[] array, int length) {
        UNSAFE.copyMemory(null, address, array, Unsafe.ARRAY_BYTE_BASE_OFFSET, length);
        return length;
    }

    static LibBz2 getLibBz2() {
        return LIB_BZ_2;
    }

    static Unsafe getUnsafe() {
        return UNSAFE;
    }

    static BZ_STREAM newBzStream() {
        return new BZ_STREAM(RUNTIME);
    }

    private static Unsafe retrieveUnsafe() {
        try {
            Class<Unsafe> clazz = sun.misc.Unsafe.class;
            Field theUnsafe = clazz.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(clazz);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildLibraryName() {
        Platform.OS os = PLATFORM.getOS();
        Platform.CPU cpu = PLATFORM.getCPU();
        switch (os) {
            case DARWIN:
                return PLATFORM.mapLibraryName("bz2");

            case LINUX:
                return PLATFORM.mapLibraryName("bz2");

            case SOLARIS:
                return PLATFORM.mapLibraryName("bz2");

            case WINDOWS:
                return extractWindowsLibrary(cpu);

            default:
                if (PLATFORM.isBSD()) {
                    return PLATFORM.mapLibraryName("bz2");
                }

                throw new RuntimeException("Unsupported operating system for bz2java");
        }
    }

    private static String extractWindowsLibrary(Platform.CPU cpu) {
        String libName = "libbz2-x86.dll";
        if (cpu == Platform.CPU.X86_64) {
            libName = "libbz2-x64.dll";
        } else if (cpu != Platform.CPU.I386) {
            throw new RuntimeException("Windows on " + cpu + " is not yet supported for bz2java");
        }
        return extractLibrary(libName);
    }

    private static String extractLibrary(String libName) {
        try {
            Path tmpPath = Files.createTempDirectory("bzip2");
            tmpPath.toFile().deleteOnExit();

            Path tmpFile = tmpPath.resolve(libName);

            try (InputStream is = Bzip2Compressor.class.getClassLoader().getResourceAsStream("lib/" + libName)) {
                Files.copy(is, tmpFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return tmpFile.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
