package ru.isa.ai.tests.tetris;

/**
 * Author: Aleksandr Panov
 * Date: 23.08.12
 * Time: 15:58
 */
public class Block implements Cloneable {
    private int xCoord;
    private int yCoord;
    private int colorIndex = 0;

    public Block(int xCoord, int yCoord) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Block newBlock = new Block(xCoord, yCoord);
        newBlock.setColorIndex(colorIndex);
        return newBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;

        if (xCoord != block.xCoord) return false;
        if (yCoord != block.yCoord) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = xCoord;
        result = 31 * result + yCoord;
        return result;
    }
}
