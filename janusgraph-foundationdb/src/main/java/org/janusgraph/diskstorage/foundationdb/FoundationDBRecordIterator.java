// Copyright 2020 JanusGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.janusgraph.diskstorage.foundationdb;

import com.apple.foundationdb.KeyValue;
import com.apple.foundationdb.subspace.Subspace;
import org.janusgraph.diskstorage.StaticBuffer;
import org.janusgraph.diskstorage.keycolumnvalue.keyvalue.KeySelector;
import org.janusgraph.diskstorage.keycolumnvalue.keyvalue.KeyValueEntry;
import org.janusgraph.diskstorage.util.RecordIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FoundationDBRecordIterator implements RecordIterator<KeyValueEntry> {
    private static final Logger log = LoggerFactory.getLogger(FoundationDBRecordIterator.class);

    protected final Subspace ds;
    protected Iterator<KeyValue> entries;
    protected final KeySelector selector;

    // Keeping track of the entries being scanned, which is different from the selected entries returned to the caller
    // due to selector
    protected int fetched;
    protected byte[] lastKey;
    protected KeyValueEntry nextKeyValueEntry;
    protected int resultSize = 0;
    protected int resultByteSize = 0;

    protected FoundationDBTx.TxGetSliceProfiler profiler;

    public FoundationDBRecordIterator(Subspace ds, final Iterator<KeyValue> keyValues, KeySelector selector,
                                      FoundationDBTx.TxGetSliceProfiler profiler) {
        this.ds = ds;
        this.selector = selector;

        entries = keyValues;
        fetched = 0;
        nextKeyValueEntry = null;
        this.profiler = profiler;

        if (this.profiler != null) {
            this.profiler.start();
        }
    }

    @Override
    public boolean hasNext() {
        fetchNext();
        if (nextKeyValueEntry == null) {
            if (profiler != null) {
                profiler.end();
                profiler.setResultSize(resultSize);
            }
        }
        return (nextKeyValueEntry != null);
    }

    @Override
    public KeyValueEntry next() {
        if (hasNext()) {
            resultSize++;
            KeyValueEntry result = nextKeyValueEntry;
            nextKeyValueEntry = null;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * No exception is captured in this base method. Instead, the iterator that extends this class will perform
     * exception handling when invoking this method.
     */
    protected void fetchNext() {
        while (nextKeyValueEntry == null && entries.hasNext()) {
            KeyValue kv = entries.next();
            fetched++;
            lastKey = kv.getKey();

            StaticBuffer key = FoundationDBKeyValueStore.getBuffer(ds.unpack(kv.getKey()).getBytes(0));
            if (selector.include(key)) {
                nextKeyValueEntry = new KeyValueEntry (key, FoundationDBKeyValueStore.getBuffer(kv.getValue()));
            }

            resultByteSize += kv.getKey().length + kv.getValue().length;
        }
    }

    @Override
    public void close() {
        // De-reference the iterator object
        entries = null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
