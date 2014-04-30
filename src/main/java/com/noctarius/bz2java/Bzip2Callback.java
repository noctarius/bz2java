package com.noctarius.bz2java;

/**
 * An instance of this interface can be provided to {@link com.noctarius.bz2java.Bzip2Compressor} or
 * {@link com.noctarius.bz2java.Bzip2Decompressor} methods to get notified on the process of the
 * compression or decompression procedure.
 */
public interface Bzip2Callback {

    /**
     * This callback method is called everytime a chunk of data is processed by the native bzip2
     * implementation.
     *
     * @param chunkBytes      Number of bytes processed in the last operation chunk
     * @param processedBytes  Number of bytes processed in total for this operation
     * @param inputByteLength Number of bytes to be processed in total before operation finishes
     */
    void callback(int chunkBytes, long processedBytes, long inputByteLength);

}
