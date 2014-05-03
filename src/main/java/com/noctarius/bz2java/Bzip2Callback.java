/**
 *    Copyright 2014 noctarius (Christoph Engelbert)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
