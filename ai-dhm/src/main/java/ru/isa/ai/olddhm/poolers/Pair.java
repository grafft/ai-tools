package ru.isa.ai.olddhm.poolers;

import org.simpleframework.xml.Element;

/**
 * Author: Aleksandr Panov
 * Date: 08.05.2014
 * Time: 11:46
 */
public class Pair<L, R> {

    @Element
    private final L left;

    @Element
    private final R right;

    public Pair(@Element(name="left") L left, @Element(name="right") R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft()) &&
                this.right.equals(pairo.getRight());
    }

}

