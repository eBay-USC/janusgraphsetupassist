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

package org.janusgraph.blueprints;

import org.apache.tinkerpop.gremlin.GraphProvider;
import org.janusgraph.FoundationDBContainer;
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration;
import org.janusgraph.diskstorage.foundationdb.FoundationDBConfigOptions;
import org.janusgraph.diskstorage.foundationdb.FoundationDBTx;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;
import org.janusgraph.graphdb.olap.computer.FulgoraGraphComputer;

import java.time.Duration;
import java.util.Set;

/**
 * @author Ted Wilmes (twilmes@gmail.com)
 */
@GraphProvider.Descriptor(computer = FulgoraGraphComputer.class)
public class FoundationDBGraphComputerProvider extends AbstractJanusGraphComputerProvider {
    public FoundationDBGraphComputerProvider() {
        FoundationDBContainer.getContainer();
    }

    @Override
    public ModifiableConfiguration getJanusGraphConfiguration(String graphName, Class<?> test, String testMethodName) {
        ModifiableConfiguration config = super.getJanusGraphConfiguration(graphName, test, testMethodName);
        config.setAll(FoundationDBContainer.getContainer().getFoundationDBConfiguration().getAll());
        config.set(FoundationDBConfigOptions.ISOLATION_LEVEL, "read_committed_with_write");
        config.set(GraphDatabaseConfiguration.IDAUTHORITY_WAIT, Duration.ofMillis(20));
        config.set(GraphDatabaseConfiguration.STORAGE_TRANSACTIONAL,false);
        return config;
    }

    @Override
    public Set<Class> getImplementations() {
        final Set<Class> implementations = super.getImplementations();
        implementations.add(FoundationDBTx.class);
        return implementations;
    }
}
