package com.noctarius.bz2java;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Bzip2Utils {

    private static final Map<String, String> SUFFIXES;

    static {
        Map<String, String> suffixes = new HashMap<>();
        suffixes.put(".tar.bz2", ".tar");
        suffixes.put(".tbz2", ".tar");
        suffixes.put(".tbz", ".tar");
        suffixes.put(".bz2", "");
        suffixes.put(".bz", "");
        SUFFIXES = Collections.unmodifiableMap(suffixes);
    }

    public static Path getCompressedFilename(Path path) {
        return Paths.get(getCompressedFilename(path.toAbsolutePath().toString()));
    }

    public static String getCompressedFilename(String filename) {
        return filename + ".bz2";
    }

    public static String getUncompressedFilename(String filename) {
        // TODO Lookup mapping :)
        return filename.replace(".bz2", "");
    }
}
