package com.noctarius.bz2java;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a helper class to support the user on mapping filenames to their compressed and decompressed
 * counterparts depending on their actual suffix.
 */
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

    /**
     * This method is used to provide the user with the compressed bzip2 filename depending on the current
     * files suffix.
     *
     * @param filename Current filename (incl. suffix) of the uncompressed file
     * @return The filename for the compressed file
     */
    public static String getCompressedFilename(String filename) {
        return filename + ".bz2";
    }

    /**
     * This method is used to provide the user with the uncompressed bzip2 filename depending on the current
     * files suffix.
     *
     * @param filename Current filename (incl. suffix) of the compressed file
     * @return The filename for the uncompressed file
     */
    public static String getUncompressedFilename(String filename) {
        // TODO Lookup mapping :)
        return filename.replace(".bz2", "");
    }
}
