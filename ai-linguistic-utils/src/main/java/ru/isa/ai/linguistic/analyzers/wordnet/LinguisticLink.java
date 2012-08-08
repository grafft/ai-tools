package ru.isa.ai.linguistic.analyzers.wordnet;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 10:23
 */
public class LinguisticLink {
    private LinguisticNode firstNode;
    private LinguisticNode secondNode;
    private String name;
    private SemanticRelationType relationType;

    public LinguisticLink(LinguisticNode firstNode, LinguisticNode secondNode, String name, SemanticRelationType relationType) {
        this.firstNode = firstNode;
        this.secondNode = secondNode;
        this.name = name;
        this.relationType = relationType;
    }

    public LinguisticNode getFirstNode() {
        return firstNode;
    }

    public void setFirstNode(LinguisticNode firstNode) {
        this.firstNode = firstNode;
    }

    public LinguisticNode getSecondNode() {
        return secondNode;
    }

    public void setSecondNode(LinguisticNode secondNode) {
        this.secondNode = secondNode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SemanticRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(SemanticRelationType relationType) {
        this.relationType = relationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinguisticLink that = (LinguisticLink) o;

        if (firstNode != null ? !firstNode.equals(that.firstNode) : that.firstNode != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (relationType != that.relationType) return false;
        if (secondNode != null ? !secondNode.equals(that.secondNode) : that.secondNode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstNode != null ? firstNode.hashCode() : 0;
        result = 31 * result + (secondNode != null ? secondNode.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (relationType != null ? relationType.hashCode() : 0);
        return result;
    }
}
