package com.noctarius.bz2java;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class CompressionTestCase {

    private static final long FILE_SIZE = 1024 * 1024 * 10;

    @Test
    public void testCompressionRoundtrip()
            throws Exception {

        Path file = TestUtils.generateRandomFile(FILE_SIZE);
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
