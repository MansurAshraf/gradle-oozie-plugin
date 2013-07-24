package org.github.mansur.oozie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author Muhammad Ashraf
 * @since 7/23/13
 */
public class DirectedGraph {

    private final List<Node> nodes;

    public DirectedGraph() {
        nodes = new ArrayList<Node>();
    }

    public void addNode(final Node node) {
        nodes.add(node);
    }

    public void addNodes(final Node... nodes) {
        Collections.addAll(this.nodes, nodes);
    }

    public List<Node> sort() {
        // Empty list that will contain the sorted elements
        final ArrayList<Node> result = new ArrayList<Node>();
        final Node head = findHead();
        add(head, result);
        return result;
    }

    private void add(final Node node, final ArrayList<Node> result) {
        result.add(node);
        if (node.outEdge != null) {
            checkCyclic(node, result);
            add(node.outEdge.to, result);
        }

    }

    private void checkCyclic(final Node node, final ArrayList<Node> result) {
        final Node to = node.outEdge.to;
        if (result.contains(to)) {
            throw new IllegalStateException(String.format("Workflow is cyclic [%s,%s]", node.toString(), to.toString()));
        }
    }

    private Node findHead() {
        final HashSet<Node> nodes = new HashSet<Node>();
        for (final Node n : this.nodes) {
            if (n.inEdges == null) {
                nodes.add(n);
            }
        }

        if (nodes.size() > 1) {
            throw new IllegalStateException("Multiple Heads Founds!" + nodes.toString());
        } else if (nodes.isEmpty()) {
            throw new IllegalStateException("No Head Founds!");
        }

        return nodes.iterator().next();
    }

    public static class Node {
        public final String name;
        public Edge inEdges;
        public Edge outEdge;

        public Node(final String name) {
            this.name = name;
        }

        public Node addEdge(final Node node) {
            final Edge e = new Edge(this, node);
            outEdge = e;
            node.inEdges = e;
            return this;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Edge {
        public final Node from;
        public final Node to;

        public Edge(final Node from, final Node to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) return false;
            if (obj.getClass() != this.getClass()) return false;
            final Edge e = (Edge) obj;
            return e.from == from && e.to == to;
        }
    }

}
