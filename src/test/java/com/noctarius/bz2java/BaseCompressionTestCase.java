package com.noctarius.bz2java;

import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

public class BaseCompressionTestCase {

    private static final long FILE_SIZE = 1024 * 1024 * 10;

    @Test
    public void testPathCompressionWithoutCallbackRoundtrip()
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

    @Test
    public void testPathCompressionWithCallbackRoundtrip()
            throws Exception {

        Path file = TestUtils.generateRandomFile(FILE_SIZE);
        Path tempPath = Files.createTempDirectory("bzip2");
        Files.createDirectories(tempPath);

        String compressedFilename = Bzip2Utils.getCompressedFilename(file.getFileName().toString());
        Path compressedFile = tempPath.resolve(compressedFilename);

        AtomicLong processedBytes = new AtomicLong();
        Bzip2Compressor.compress(file, compressedFile, buildCallback(processedBytes));
        assertEquals(Files.size(file), processedBytes.get());

        String uncompressedFilename = Bzip2Utils.getUncompressedFilename(compressedFilename);
        Path uncompressedFile = tempPath.resolve(uncompressedFilename);

        Bzip2Decompressor.decompress(compressedFile, uncompressedFile, buildCallback(processedBytes));
        assertEquals(Files.size(compressedFile), processedBytes.get());

        String originalHash = TestUtils.hash(file);
        String uncompressedHash = TestUtils.hash(uncompressedFile);
        assertEquals(originalHash, uncompressedHash);
    }

    @Test
    public void testInputOutputStreamCompressionWithoutCallbackRoundtrip()
            throws Exception {

        Path file = TestUtils.generateRandomFile(FILE_SIZE);
        Path tempPath = Files.createTempDirectory("bzip2");
        Files.createDirectories(tempPath);

        String compressedFilename = Bzip2Utils.getCompressedFilename(file.getFileName().toString());
        Path compressedFile = tempPath.resolve(compressedFilename);

        try (InputStream inputStream = Files.newInputStream(file);
             OutputStream outputStream = Files.newOutputStream(compressedFile, StandardOpenOption.CREATE)) {

            Bzip2Compressor.compress(inputStream, outputStream);
        }

        String uncompressedFilename = Bzip2Utils.getUncompressedFilename(compressedFilename);
        Path uncompressedFile = tempPath.resolve(uncompressedFilename);

        try (InputStream inputStream = Files.newInputStream(compressedFile);
             OutputStream outputStream = Files.newOutputStream(uncompressedFile, StandardOpenOption.CREATE)) {

            Bzip2Decompressor.decompress(inputStream, outputStream);
        }

        String originalHash = TestUtils.hash(file);
        String uncompressedHash = TestUtils.hash(uncompressedFile);
        assertEquals(originalHash, uncompressedHash);
    }

    @Test
    public void testInputOutputStreamCompressionWithCallbackRoundtrip()
            throws Exception {

        Path file = TestUtils.generateRandomFile(FILE_SIZE);
        Path tempPath = Files.createTempDirectory("bzip2");
        Files.createDirectories(tempPath);

        String compressedFilename = Bzip2Utils.getCompressedFilename(file.getFileName().toString());
        Path compressedFile = tempPath.resolve(compressedFilename);

        try (InputStream inputStream = Files.newInputStream(file);
             OutputStream outputStream = Files.newOutputStream(compressedFile, StandardOpenOption.CREATE)) {

            long fileSize = Files.size(file);
            AtomicLong processedBytes = new AtomicLong();
            Bzip2Compressor.compress(inputStream, outputStream, fileSize, buildCallback(processedBytes));
            assertEquals(fileSize, processedBytes.get());
        }

        String uncompressedFilename = Bzip2Utils.getUncompressedFilename(compressedFilename);
        Path uncompressedFile = tempPath.resolve(uncompressedFilename);

        try (InputStream inputStream = Files.newInputStream(compressedFile);
             OutputStream outputStream = Files.newOutputStream(uncompressedFile, StandardOpenOption.CREATE)) {

            long fileSize = Files.size(compressedFile);
            AtomicLong processedBytes = new AtomicLong();
            Bzip2Decompressor.decompress(inputStream, outputStream, fileSize, buildCallback(processedBytes));
            assertEquals(fileSize, processedBytes.get());
        }

        String originalHash = TestUtils.hash(file);
        String uncompressedHash = TestUtils.hash(uncompressedFile);
        assertEquals(originalHash, uncompressedHash);
    }

    private Bzip2Callback buildCallback(final AtomicLong processedBytes) {
        return new Bzip2Callback() {

            @Override
            public void callback(int chunkBytes, long pb, long inputByteLength) {
                processedBytes.set(pb);
            }
        };
    }
}
