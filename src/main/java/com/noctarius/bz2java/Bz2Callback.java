package com.noctarius.bz2java;

public interface Bz2Callback {

    void callback(int chunkBytes, long processedBytes, long inputByteLength);

}
