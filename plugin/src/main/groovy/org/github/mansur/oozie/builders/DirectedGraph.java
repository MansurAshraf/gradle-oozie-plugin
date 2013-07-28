package org.github.mansur.oozie.builders;

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
        if (result.contains(node)) {
            result.remove(node);
            for (final Edge edge : node.outEdge) {
                remove(edge.to, result);
            }
        }

        result.add(node);
        if (!node.outEdge.isEmpty()) {
            checkCyclic(node, result);
            for (final Edge edge : node.outEdge) {
                add(edge.to, result);
            }

        }

    }

    private void remove(final Node n, final ArrayList<Node> result) {
        result.remove(n);
        final HashSet<Edge> outEdge = n.outEdge;
        for (final Edge edge : outEdge) {
            remove(edge.to, result);
        }
    }

    private void checkCyclic(final Node node, final ArrayList<Node> result) {
        if (!node.outEdge.isEmpty()
                && result.contains(node.outEdge.iterator().next().to)
                && !node.outEdge.iterator().next().to.type.equals("join")
                && !node.outEdge.iterator().next().to.type.equals("kill")) {
            throw new IllegalStateException(String.format("Workflow is cyclic [%s,%s]", node.toString(), node.outEdge.iterator().next().to));
        }
    }

    private Node findHead() {
        final HashSet<Node> nodes = new HashSet<Node>();
        for (final Node n : this.nodes) {
            if (n.inEdges.size() == 0) {
                nodes.add(n);
            }
        }

        if (nodes.size() > 1) {
            throw new IllegalStateException("Multiple Starting nodes Founds!" + nodes.toString());
        } else if (nodes.isEmpty()) {
            throw new IllegalStateException("No Starting node Found!");
        }

        return nodes.iterator().next();
    }

    public static class Node {
        private final String name;
        private final String type;
        private final HashSet<Edge> inEdges = new HashSet<Edge>();
        private final HashSet<Edge> outEdge = new HashSet<Edge>();

        public Node(final String name, final String type) {
            this.name = name;
            this.type = type;
        }

        public Node addEdge(final Node node) {
            final Edge e = new Edge(this, node);
            outEdge.add(e);
            node.inEdges.add(e);
            return this;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Node node = (Node) o;

            return !(name != null ? !name.equals(node.name) : node.name != null);

        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
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
