package com.noctarius.bz2java.jnr;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class BZ_STREAM
        extends Struct {

    public final Pointer next_in = new Pointer();
    public final Unsigned32 avail_in = new Unsigned32();
    public final Unsigned32 total_in_lo32 = new Unsigned32();
    public final Unsigned32 total_in_hi32 = new Unsigned32();

    public final Pointer next_out = new Pointer();
    public final Unsigned32 avail_out = new Unsigned32();
    public final Unsigned32 total_out_lo32 = new Unsigned32();
    public final Unsigned32 total_out_hi32 = new Unsigned32();

    public final Pointer state = new Pointer();

    public final Pointer bzalloc = new Pointer();
    public final Pointer bzfree = new Pointer();
    public final Pointer opaque = new Pointer();

    public BZ_STREAM(Runtime runtime) {
        super(runtime);
    }
}
