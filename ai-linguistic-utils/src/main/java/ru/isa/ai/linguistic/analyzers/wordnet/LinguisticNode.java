package ru.isa.ai.linguistic.analyzers.wordnet;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 10:41
 */
public class LinguisticNode {
    private String node;
    private WordType type;

    public LinguisticNode(String node, WordType type) {
        this.node = node;
        this.type = type;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public WordType getType() {
        return type;
    }

    public void setType(WordType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinguisticNode that = (LinguisticNode) o;

        if (node != null ? !node.equals(that.node) : that.node != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = node != null ? node.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
