package ru.isa.ai.linguistic.analyzers.wordnet;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 10:22
 */
public class LinguisticWordNet {
    private Set<LinguisticLink> links;
    private Map<Integer, LinguisticNode> nodes;
    private LinguisticNode mainNode;

    public LinguisticWordNet() {
        links = new HashSet<>();
        nodes = new HashMap<>();
    }

    public Set<LinguisticLink> getLinks() {
        return links;
    }

    public void addLink(LinguisticLink link) {
        int firstHashCode = link.getFirstNode().hashCode();
        int secondHashCode = link.getSecondNode().hashCode();
        if (nodes.containsKey(firstHashCode)) {
            link.setFirstNode(nodes.get(firstHashCode));
        }
        if (nodes.containsKey(secondHashCode)) {
            link.setSecondNode(nodes.get(secondHashCode));
        }
        links.add(link);
        nodes.put(firstHashCode, link.getFirstNode());
        nodes.put(secondHashCode, link.getSecondNode());
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
        List<LinguisticLink> sortedLinks = new ArrayList<>(links);
        Collections.sort(sortedLinks);
        for (LinguisticLink link : sortedLinks) {
            result.append(String.format("%s<-(%s)-%s\n", link.getFirstNode().getNode(), link.getName(), link.getSecondNode().getNode()));
        }
        return result.toString();
    }
}
