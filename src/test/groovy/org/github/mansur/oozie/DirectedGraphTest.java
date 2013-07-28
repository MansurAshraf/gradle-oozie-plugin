/*
 * Copyright 2013. Muhammad Ashraf
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.github.mansur.oozie;

import org.github.mansur.oozie.builders.DirectedGraph;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Muhammad Ashraf
 * @since 7/23/13
 */
public class DirectedGraphTest {

    @Test
    public void testSort() throws Exception {
        final DirectedGraph.Node node1 = new DirectedGraph.Node("node1", "typeo");
        final DirectedGraph.Node node2 = new DirectedGraph.Node("node2", "type");
        final DirectedGraph.Node node3 = new DirectedGraph.Node("node3", "type");
        final DirectedGraph.Node node4 = new DirectedGraph.Node("node4", "type");
        node1.addEdge(node2);
        node2.addEdge(node3);
        node3.addEdge(node4);

        final DirectedGraph directedGraph = new DirectedGraph();
        directedGraph.addNodes(node2, node1, node3, node4);
        final List<DirectedGraph.Node> sortedGraph = directedGraph.sort();
        assertThat(sortedGraph).containsExactly(node1, node2, node3, node4);
    }

    @Test
    public void testOneNodeFlow() throws Exception {
        final DirectedGraph.Node node1 = new DirectedGraph.Node("node1", "type");

        final DirectedGraph directedGraph = new DirectedGraph();
        directedGraph.addNodes(node1);
        final List<DirectedGraph.Node> sortedGraph = directedGraph.sort();
        assertThat(sortedGraph).containsExactly(node1);

    }

    @Test(expected = IllegalStateException.class)
    public void testMultipleHeads() throws Exception {
        final DirectedGraph.Node node1 = new DirectedGraph.Node("node1", "type");
        final DirectedGraph.Node node2 = new DirectedGraph.Node("node2", "type");
        final DirectedGraph.Node node3 = new DirectedGraph.Node("node3", "type");
        final DirectedGraph.Node node4 = new DirectedGraph.Node("node4", "type");
        node2.addEdge(node3);
        node3.addEdge(node4);

        final DirectedGraph directedGraph = new DirectedGraph();
        directedGraph.addNodes(node2, node1, node3, node4);
        directedGraph.sort();
    }

    @Test(expected = IllegalStateException.class)
    public void testCyclicGraph() throws Exception {
        final DirectedGraph.Node node1 = new DirectedGraph.Node("node1", "type");
        final DirectedGraph.Node node2 = new DirectedGraph.Node("node2", "type");
        final DirectedGraph.Node node3 = new DirectedGraph.Node("node3", "type");
        node1.addEdge(node2);
        node2.addEdge(node3);
        node3.addEdge(node2);

        final DirectedGraph directedGraph = new DirectedGraph();
        directedGraph.addNodes(node2, node1, node3);
        directedGraph.sort();

    }
}
