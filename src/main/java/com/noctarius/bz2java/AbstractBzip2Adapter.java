package com.noctarius.bz2java;

import com.noctarius.bz2java.jnr.LibBz2;
import sun.misc.Unsafe;

abstract class AbstractBzip2Adapter {

    protected static final int BUFFER_SIZE = 1024 * 1024;

    protected static final Unsafe UNSAFE = NativeUtils.getUnsafe();
    protected static final LibBz2 LIB_BZ_2 = NativeUtils.getLibBz2();

    protected int copyToNative(byte[] array, long address, int length) {
        UNSAFE.copyMemory(array, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, address, length);
        return length;
    }

    protected int copyFromNative(long address, byte[] array, int length) {
        UNSAFE.copyMemory(null, address, array, Unsafe.ARRAY_BYTE_BASE_OFFSET, length);
        return length;
    }

    protected long allocateMemory(int bufferSize) {
        return UNSAFE.allocateMemory(bufferSize);
    }

    protected void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    protected void handleNativeError(int result) {
        switch (result) {
            case LibBz2.BZ_MEM_ERROR:
                throw new OutOfMemoryError("Out of native memory for compression / decompression");

            case LibBz2.BZ_PARAM_ERROR:
                throw new IllegalArgumentException("Illegal parameter in native code");

            default:
                throw new InternalError("Native error: " + result);
        }
    }
}
