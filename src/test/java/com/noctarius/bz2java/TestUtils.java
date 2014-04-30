package com.noctarius.bz2java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Random;

final class TestUtils {

    private TestUtils() {
    }

    static Path generateRandomFile(long fileSize) {
        try {
            Random random = new Random();
            Path tempPath = Files.createTempFile("bz2java", "dat");
            File tempFile = tempPath.toFile();
            tempFile.deleteOnExit();

            byte[] buffer = new byte[NativeUtils.BUFFER_SIZE];
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                long remains = fileSize;
                while (remains > 0) {
                    random.nextBytes(buffer);

                    int chunkSize = (int) Math.min(NativeUtils.BUFFER_SIZE, remains);
                    out.write(buffer, 0, chunkSize);
                    remains -= chunkSize;
                }
            }

            return tempPath;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String hash(Path file) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            readFile(file, new FileConsumer() {
                @Override
                public void handle(byte[] buffer, int bytes)
                        throws Exception {

                    md.update(buffer, 0, bytes);
                }
            });
            return new BigInteger(1, md.digest()).toString(16).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void readFile(Path file, FileConsumer consumer)
            throws IOException {

        byte[] buffer = new byte[NativeUtils.BUFFER_SIZE];
        try (InputStream is = Files.newInputStream(file)) {
            int bytes;
            while ((bytes = is.read(buffer)) != -1) {
                consumer.handle(buffer, bytes);
            }
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(e);
        }
    }

    static interface FileConsumer {

        void handle(byte[] buffer, int bytes)
                throws Exception;
    }
}
