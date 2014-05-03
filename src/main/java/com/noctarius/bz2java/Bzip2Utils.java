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
        String lcFilename = filename.toLowerCase();
        for (Map.Entry<String, String> entry : SUFFIXES.entrySet()) {
            if (lcFilename.endsWith(entry.getKey())) {
                int suffixLength = entry.getKey().length();
                return filename.substring(0, filename.length() - suffixLength) + entry.getValue();
            }
        }
        return filename.replace(".bz2", "");
    }
}
