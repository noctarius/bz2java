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
