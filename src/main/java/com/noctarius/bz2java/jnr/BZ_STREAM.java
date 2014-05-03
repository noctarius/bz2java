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
