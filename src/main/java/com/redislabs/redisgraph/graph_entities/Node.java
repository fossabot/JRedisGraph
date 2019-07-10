package com.redislabs.redisgraph.graph_entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * * A class represent an node (graph entity). In addition to the base class id and properties, a node has labels.
 */
public class Node extends GraphEntity {

    //members
    final private List<String> labels = new ArrayList<>();

    /**
     * @param label - a label to be add
     */
    public void addLabel(String label) {
        labels.add(label);
    }

    /**
     * @param label - a label to be removed
     */
    public void removeLabel(String label) {
        labels.remove(label);
    }

    /**
     * @param index - label index
     * @return the property label
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   ({@code index < 0 || index >= getNumberOfLabels()})
     */
    public String getLabel(int index) throws IndexOutOfBoundsException{
        return labels.get(index);
    }

    /**
     *
     * @return the number of labels
     */
    public int getNumberOfLabels() {
        return labels.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        if (!super.equals(o)) return false;
        Node node = (Node) o;
        return Objects.equals(labels, node.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), labels);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Node{");
        sb.append("labels=").append(labels);
        sb.append(", id=").append(id);
        sb.append(", propertyMap=").append(propertyMap);
        sb.append('}');
        return sb.toString();
    }
}
