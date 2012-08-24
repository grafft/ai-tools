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
}
