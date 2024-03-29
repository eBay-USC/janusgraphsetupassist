// Copyright 2018 Expero Inc.
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

import org.janusgraph.FoundationDBContainer;
import org.janusgraph.diskstorage.BackendException;
import org.janusgraph.diskstorage.KeyColumnValueStoreTest;
import org.janusgraph.diskstorage.keycolumnvalue.KeyColumnValueStoreManager;
import org.janusgraph.diskstorage.keycolumnvalue.keyvalue.OrderedKeyValueStoreManagerAdapter;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author Ted Wilmes (twilmes@gmail.com)
 */
@Testcontainers
public class FoundationDBVariableLengthKCVSTest extends KeyColumnValueStoreTest {
    @Container
    public static FoundationDBContainer container = new FoundationDBContainer();

    public KeyColumnValueStoreManager openStorageManager() throws BackendException {
        FoundationDBStoreManager sm = new FoundationDBStoreManager(container.getFoundationDBConfiguration());
        return new OrderedKeyValueStoreManagerAdapter(sm);
    }
}
