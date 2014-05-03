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

import static org.junit.Assert.assertEquals;

public class Bzip2UtilsTestCase {

    @Test
    public void testCompressedFilename() {
        assertEquals("foo.tar.bz2", Bzip2Utils.getCompressedFilename("foo.tar"));
        assertEquals("foo.bz2", Bzip2Utils.getCompressedFilename("foo"));
    }

    @Test
    public void testUncompressedFilename() {
        assertEquals("foo.tar", Bzip2Utils.getUncompressedFilename("foo.tar.bz2"));
        assertEquals("foo.tar", Bzip2Utils.getUncompressedFilename("foo.tbz2"));
        assertEquals("foo.tar", Bzip2Utils.getUncompressedFilename("foo.tbz"));
        assertEquals("foo", Bzip2Utils.getUncompressedFilename("foo.bz2"));
        assertEquals("foo", Bzip2Utils.getUncompressedFilename("foo.bz"));
    }
}
