package com.noctarius.bz2java;

import com.noctarius.bz2java.jnr.BZ_STREAM;
import com.noctarius.bz2java.jnr.LibBz2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static com.noctarius.bz2java.NativeUtils.BUFFER_SIZE;
import static com.noctarius.bz2java.NativeUtils.allocateMemory;
import static com.noctarius.bz2java.NativeUtils.copyFromNative;
import static com.noctarius.bz2java.NativeUtils.copyToNative;
import static com.noctarius.bz2java.NativeUtils.freeMemory;
import static com.noctarius.bz2java.NativeUtils.getLibBz2;
import static com.noctarius.bz2java.NativeUtils.handleNativeError;
import static com.noctarius.bz2java.NativeUtils.newBzStream;

/**
 * <p>
 * This class implements the binding logic to handle decompression of bz2 in native code. The implementation
 * itself is fully threadsafe and multiple decompression calls can be executed in parallel only limited by
 * available RAM since the bz2 compression algorithm isn't very memory efficient but fast and pretty good
 * in compression ratio.
 * </p>
 * <p>
 * A common example on how to use it is like the below:
 * <pre>
 *     Path sourcePath = Paths.get("/my/input/file.tar");
 *     Path targetPath = Paths.get("/my/target/path/");
 *     Path outputPath = Bzip2Utils.getUncompressedFilename(targetPath, sourcePath.getFilename());
 *     Bzip2Decompressor.decompress(sourcePath, outputPath, buildCallback());
 * </pre>
 * </p>
 */
public class Bzip2Decompressor {

    private static final LibBz2 LIB_BZ_2 = getLibBz2();

    public static void decompress(Path source, Path target)
            throws IOException {

        decompress(source, target, null);
    }

    public static void decompress(Path source, Path target, Bzip2Callback callback)
            throws IOException {

        long fileLength = Files.size(source);
        try (FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
             FileChannel targetChannel = FileChannel.open(target, StandardOpenOption.CREATE)) {

            decompress(sourceChannel, targetChannel, fileLength, callback);
        }
    }

    public static void decompress(InputStream input, OutputStream output)
            throws IOException {

        decompress(input, output, -1, null);
    }

    public static void decompress(InputStream input, OutputStream output, long inputByteLength, Bzip2Callback callback)
            throws IOException {

        ReadableByteChannel inputChannel = Channels.newChannel(input);
        WritableByteChannel outputChannel = Channels.newChannel(output);
        decompress(inputChannel, outputChannel, inputByteLength, callback);
    }

    public static void decompress(ReadableByteChannel input, WritableByteChannel output)
            throws IOException {

        decompress(input, output, -1, null);
    }

    public static void decompress(ReadableByteChannel input, WritableByteChannel output, long inputByteLength,
                                  Bzip2Callback callback)
            throws IOException {

        long processed = 0;

        // Initialize native library
        BZ_STREAM bzStream = newBzStream();

        int result = LIB_BZ_2.BZ2_bzDecompressInit(bzStream, 1, 0);
        if (result != LibBz2.BZ_OK) {
            handleNativeError(result);
        }

        // Allocate native memory for operation on library
        long inputPtr = allocateMemory(BUFFER_SIZE);
        long outputPtr = allocateMemory(BUFFER_SIZE);

        try {
            byte[] outByteArray = new byte[BUFFER_SIZE];
            byte[] inByteArray = new byte[BUFFER_SIZE];
            ByteBuffer inputBuffer = ByteBuffer.wrap(inByteArray);

            int bytes;
            while ((bytes = input.read(inputBuffer)) != -1) {
                // Copy input data to native memory
                int availBytes = inputBuffer.position();
                copyToNative(inByteArray, inputPtr, availBytes);

                // Call decompression
                bzDecompress(bzStream, inputPtr, outputPtr, outByteArray, output, availBytes, inputBuffer);

                // Call callback if set
                if (callback != null) {
                    processed += bytes;
                    callback.callback(bytes, processed, inputByteLength);
                }
            }

            // Read final decompressed data
            inputBuffer.position(0);
            inputBuffer.limit(0);

            boolean finish;
            do {
                // Call decompression until all data is written
                finish = bzDecompress(bzStream, inputPtr, outputPtr, outByteArray, output, 0, inputBuffer);
            } while (!finish);

            // Finish decompression
            LIB_BZ_2.BZ2_bzDecompressEnd(bzStream);

        } finally {
            freeMemory(inputPtr);
            freeMemory(outputPtr);
        }
    }

    private static boolean bzDecompress(BZ_STREAM bzStream, long inputPtr, long outputPtr, byte[] outByteArray, //
                                        WritableByteChannel output, int availBytes, ByteBuffer inputBuffer)
            throws IOException {

        // Set available byte sizes
        bzStream.avail_in.set(availBytes);
        bzStream.avail_out.set(BUFFER_SIZE);

        // Set memory areas
        bzStream.next_in.set(inputPtr);
        bzStream.next_out.set(outputPtr);

        // Compress data
        int result = LIB_BZ_2.BZ2_bzDecompress(bzStream);
        switch (result) {
            case LibBz2.BZ_STREAM_END:
                if (availBytes > 0) {
                    throw new IllegalStateException();
                }
                break;

            case LibBz2.BZ_RUN_OK:
            case LibBz2.BZ_FLUSH_OK:
            case LibBz2.BZ_FINISH_OK:
                break;

            default:
                throw new InternalError("Error while compression: " + result);
        }

        // Reposition and compact the buffer
        inputBuffer.position(availBytes - bzStream.avail_in.intValue());
        inputBuffer.compact();

        // Copy from native memory
        int compressedLength = BUFFER_SIZE - bzStream.avail_out.intValue();
        copyFromNative(outputPtr, outByteArray, compressedLength);

        // Write to file
        ByteBuffer outputBuffer = ByteBuffer.wrap(outByteArray, 0, compressedLength);
        output.write(outputBuffer);
        return result == LibBz2.BZ_STREAM_END;
    }
}
