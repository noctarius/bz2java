package com.noctarius.bz2java;

public interface Bzip2Callback {

    void callback(int chunkBytes, long processedBytes, long inputByteLength);

}
