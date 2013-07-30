package ru.isa.ai.linguistic.analyzers.wordnet;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 10:22
 */
public class LinguisticWordNet {
    private Map<Integer, LinguisticLink> links;
    private Map<Integer, LinguisticNode> nodes;
    private LinguisticNode mainNode;

    public LinguisticWordNet() {
        links = new HashMap<>();
        nodes = new HashMap<>();
    }

    public Set<LinguisticLink> getLinks() {
        return new HashSet<>(links.values());
    }

    public void addLink(LinguisticLink link) {
        int firstHashCode = link.getFirstNode().hashCode();
        int secondHashCode = link.getSecondNode().hashCode();
        if (nodes.containsKey(firstHashCode)) {
            link.setFirstNode(nodes.get(firstHashCode));
        } else {
            nodes.put(firstHashCode, link.getFirstNode());
        }
        if (nodes.containsKey(secondHashCode)) {
            link.setSecondNode(nodes.get(secondHashCode));
        } else {
            nodes.put(secondHashCode, link.getSecondNode());
        }
        if (links.containsKey(link.hashCode())) {
            LinguisticLink existed = links.get(link.hashCode());
            existed.setWeight(existed.getWeight() + 1);
        } else {
            links.put(link.hashCode(), link);
        }

    }

    public Set<LinguisticNode> getNodes() {
        return new HashSet<>(nodes.values());
    }

    public LinguisticNode getMainNode() {
        return mainNode;
    }

    public void setMainNode(LinguisticNode mainNode) {
        this.mainNode = mainNode;
    }

    public void injection(LinguisticWordNet network) {
        for (LinguisticLink link : network.getLinks()) {
            this.addLink(link);
        }
        if (network.mainNode != null) {
            this.mainNode = network.mainNode;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        List<LinguisticLink> sortedLinks = new ArrayList<>(links.values());
        Collections.sort(sortedLinks);
        for (LinguisticLink link : sortedLinks) {
            result.append(link.toString()).append("\n");
        }
        return result.toString();
    }
}
