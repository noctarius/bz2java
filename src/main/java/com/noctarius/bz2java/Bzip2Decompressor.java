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
import static com.noctarius.bz2java.NativeUtils.WRITE_OPEN_OPTIONS;
import static com.noctarius.bz2java.NativeUtils.allocateMemory;
import static com.noctarius.bz2java.NativeUtils.copyFromNative;
import static com.noctarius.bz2java.NativeUtils.copyToNative;
import static com.noctarius.bz2java.NativeUtils.freeMemory;
import static com.noctarius.bz2java.NativeUtils.getLibBz2;
import static com.noctarius.bz2java.NativeUtils.handleNativeError;
import static com.noctarius.bz2java.NativeUtils.newBzStream;

/**
 * <p>
 * This class implements the binding logic to handle compression of bz2 in native code. The implementation
 * itself is fully threadsafe and multiple compression calls can be executed in parallel only limited by
 * available RAM since the bz2 compression algorithm isn't very memory efficient but fast and pretty good
 * in compression ratio.
 * </p>
 * <p>
 * A common example on how to use it to directly compress a file using {@link java.nio.file.Path}s is like
 * the one below:
 * <pre>
 *     Path sourcePath = Paths.get("/my/input/file.tar");
 *     Path targetPath = Paths.get("/my/target/path/");
 *     String uncompressedFilename = Bzip2Utils.getUncompressedFilename(sourcePath.getFilename());
 *     Path outputPath = targetPath.resolve(sourcePath.getFilename());
 *     Bzip2Compressor.compress(sourcePath, outputPath, buildCallback());
 * </pre>
 * </p>
 * <p>
 * There are also more options available like using {@link java.io.InputStream} and {@link java.io.OutputStream}
 * or {@link java.nio.channels.ReadableByteChannel} and {@link java.nio.channels.WritableByteChannel} for input
 * and output.
 * </p>
 */
public final class Bzip2Decompressor {

    private static final LibBz2 LIB_BZ_2 = getLibBz2();

    private Bzip2Decompressor() {
    }

    /**
     * Decompresses an input file supplied though a {@link java.nio.file.Path} reference and writes the uncompressed
     * bytestream to the given output path.
     *
     * @param source Input path (file) to read compressed data from
     * @param target Output path (file) to write uncompressed data to
     * @throws IOException If an filesystem or decompression problem is raised this exception is thrown
     */
    public static void decompress(Path source, Path target)
            throws IOException {

        decompress(source, target, null);
    }

    /**
     * Decompresses an input file supplied though a {@link java.nio.file.Path} reference and writes the uncompressed
     * bytestream to the given output path. For every processed data chunk the caller gets notified using the
     * callback instance.<br/>
     * If callback is null it will silently ignored (a call with null parameter is equivalent to
     * {@link #decompress(java.nio.file.Path, java.nio.file.Path)}.
     *
     * @param source   Input path (file) to read compressed data from
     * @param target   Output path (file) to write uncompressed data to
     * @param callback The callback to notify on decompression progress or null to work silently
     * @throws IOException If an filesystem or decompression problem is raised this exception is thrown
     */
    public static void decompress(Path source, Path target, Bzip2Callback callback)
            throws IOException {

        long fileLength = Files.size(source);
        try (FileChannel sourceChannel = FileChannel.open(source, StandardOpenOption.READ);
             FileChannel targetChannel = FileChannel.open(target, WRITE_OPEN_OPTIONS)) {

            decompress(sourceChannel, targetChannel, fileLength, callback);
        }
    }

    /**
     * Decompresses an inputstream supplied though a {@link java.io.InputStream} reference and writes the
     * uncompressed bytestream to the given output stream.
     *
     * @param input  Input stream to read compressed data from
     * @param output Output stream to write uncompressed data to
     * @throws IOException If an filesystem or decompression problem is raised this exception is thrown
     */
    public static void decompress(InputStream input, OutputStream output)
            throws IOException {

        decompress(input, output, -1, null);
    }

    /**
     * Decompresses an inputstream supplied though a {@link java.io.InputStream} reference and writes the
     * uncompressed bytestream to the given output stream. For every processed data chunk the caller gets
     * notified using the callback instance.<br/>
     * If callback is null it will silently ignored (a call with null parameter is equivalent to
     * {@link #decompress(java.io.InputStream, java.io.OutputStream)}.
     *
     * @param input           Input stream to read compressed data from
     * @param output          Output stream to write uncompressed data to
     * @param inputByteLength Defines the length of the complete input data to calculate percentage values on callback
     * @param callback        The callback to notify on decompression progress or null to work silently
     * @throws IOException If an filesystem or decompression problem is raised this exception is thrown
     */
    public static void decompress(InputStream input, OutputStream output, long inputByteLength, Bzip2Callback callback)
            throws IOException {

        ReadableByteChannel inputChannel = Channels.newChannel(input);
        WritableByteChannel outputChannel = Channels.newChannel(output);
        decompress(inputChannel, outputChannel, inputByteLength, callback);
    }

    /**
     * Decompresses an input bytechannel supplied though a {@link java.nio.channels.ReadableByteChannel} reference
     * and writes the uncompressed bytestream to the given output channel.
     *
     * @param input  Input bytechannel to read compressed data from
     * @param output Output bytechannel to write uncompressed data to
     * @throws IOException If an filesystem or decompression problem is raised this exception is thrown
     */
    public static void decompress(ReadableByteChannel input, WritableByteChannel output)
            throws IOException {

        decompress(input, output, -1, null);
    }

    /**
     * Deompresses an input bytechannel supplied though a {@link java.nio.channels.ReadableByteChannel} reference
     * and writes the uncompressed bytestream to the given output channel. For every processed data chunk the caller
     * gets notified using the callback instance.<br/>
     * If callback is null it will silently ignored (a call with null parameter is equivalent to
     * {@link #decompress(java.nio.channels.ReadableByteChannel, java.nio.channels.WritableByteChannel)}.
     *
     * @param input           Input bytechannel to read compressed data from
     * @param output          Output bytechannel to write uncompressed data to
     * @param inputByteLength Defines the length of the complete input data to calculate percentage values on callback
     * @param callback        The callback to notify on decompression progress or null to work silently
     * @throws IOException If an filesystem or decompression problem is raised this exception is thrown
     */
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
            boolean finish = false;
            while ((bytes = input.read(inputBuffer)) != -1 && !finish) {
                // Copy input data to native memory
                int availBytes = inputBuffer.position();
                copyToNative(inByteArray, inputPtr, availBytes);

                // Call decompression
                finish = bzDecompress(bzStream, inputPtr, outputPtr, outByteArray, output, availBytes, inputBuffer);

                // Call callback if set
                if (callback != null) {
                    processed += bytes;
                    callback.callback(bytes, processed, inputByteLength);
                }
            }

            // Read final decompressed data
            inputBuffer.position(0);
            inputBuffer.limit(0);

            while (!finish) {
                // Call decompression until all data is written
                finish = bzDecompress(bzStream, inputPtr, outputPtr, outByteArray, output, 0, inputBuffer);
            }

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
            case LibBz2.BZ_OK:
                break;

            default:
                throw new InternalError("Error while decompression: " + result);
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
