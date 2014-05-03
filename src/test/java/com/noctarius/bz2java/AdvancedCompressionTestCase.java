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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AdvancedCompressionTestCase {

    @Parameterized.Parameters(name = "testCompressionVariousFilesizes -  {index}, fileSize: {0} bytes")
    public static Iterable<Object[]> parameters() {
        return Arrays.asList(new Object[][]{ //
                                             {1024}, {2048}, {4096}, {8192}, {1024 * 1024}, //
                                             {2048 * 1024}, {4096 * 1024}, {8192 * 1024}, //
                                             {1024 * 1024 * 10}, {1024 * 1024 * 100} //
        });
    }

    private final int fileSize;

    public AdvancedCompressionTestCase(int fileSize) {
        this.fileSize = fileSize;
    }

    @Test
    public void testCompressionVariousFilesizes()
            throws Exception {

        Path file = TestUtils.generateRandomFile(fileSize);
        Path tempPath = Files.createTempDirectory("bzip2");
        Files.createDirectories(tempPath);

        String compressedFilename = Bzip2Utils.getCompressedFilename(file.getFileName().toString());
        Path compressedFile = tempPath.resolve(compressedFilename);

        Bzip2Compressor.compress(file, compressedFile);

        String uncompressedFilename = Bzip2Utils.getUncompressedFilename(compressedFilename);
        Path uncompressedFile = tempPath.resolve(uncompressedFilename);

        Bzip2Decompressor.decompress(compressedFile, uncompressedFile);

        String originalHash = TestUtils.hash(file);
        String uncompressedHash = TestUtils.hash(uncompressedFile);
        assertEquals(originalHash, uncompressedHash);
    }
}
