package com.noctarius.bz2java.jnr;

import jnr.ffi.annotations.Direct;

public interface LibBz2 {

    public static final int BZ_RUN = 0;
    public static final int BZ_FLUSH = 1;
    public static final int BZ_FINISH = 2;

    public static final int BZ_OK = 0;
    public static final int BZ_RUN_OK = 1;
    public static final int BZ_FLUSH_OK = 2;
    public static final int BZ_FINISH_OK = 3;
    public static final int BZ_STREAM_END = 4;
    public static final int BZ_SEQUENCE_ERROR = (-1);
    public static final int BZ_PARAM_ERROR = (-2);
    public static final int BZ_MEM_ERROR = (-3);
    public static final int BZ_DATA_ERROR = (-4);
    public static final int BZ_DATA_ERROR_MAGIC = (-5);
    public static final int BZ_IO_ERROR = (-6);
    public static final int BZ_UNEXPECTED_EOF = (-7);
    public static final int BZ_OUTBUFF_FULL = (-8);
    public static final int BZ_CONFIG_ERROR = (-9);

    int BZ2_bzCompressInit(@Direct BZ_STREAM bzStream, int blockSize100k, int verbosity, int workFactor);

    int BZ2_bzCompress(BZ_STREAM bzStream, int action);

    int BZ2_bzCompressEnd(BZ_STREAM bzStream);

    int BZ2_bzDecompressInit(@Direct BZ_STREAM bzStream, int verbosity, int small);

    int BZ2_bzDecompress(BZ_STREAM bzStream);

    int BZ2_bzDecompressEnd(BZ_STREAM bzStream);

}
