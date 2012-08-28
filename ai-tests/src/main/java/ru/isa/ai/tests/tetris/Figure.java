package ru.isa.ai.tests.tetris;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 23.08.12
 * Time: 15:13
 */
public class Figure implements Cloneable {
    private int xSize;
    private int ySize;
    private byte[] form;
    private List<Block> blocks = new ArrayList<>();
    private int xCoord = 0;
    private int yCoord = 0;
    private byte orientation = 0;

    public Figure(int xSize, int ySize, byte[] form) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.form = form;
        for (int j = 0; j < ySize; j++) {
            for (int i = 0; i < xSize; i++) {
                if (form[xSize * j + i] == 1) {
                    blocks.add(new Block(i, j));
                }
            }
        }
    }

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }

    public byte[] getForm() {
        return form;
    }

    public void setForm(byte[] form) {
        this.form = form;
    }


    public byte getOrientation() {
        return orientation;
    }

    public void setOrientation(byte orientation) {
        this.orientation = orientation;
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

    @Override
    protected Figure clone() {
        byte[] newForm = new byte[xSize * ySize];
        System.arraycopy(form, 0, newForm, 0, xSize * ySize);
        return new Figure(xSize, ySize, newForm);
    }

    public void rotate() {
        byte[] newForm = new byte[xSize * ySize];
        System.arraycopy(form, 0, newForm, 0, xSize * ySize);
        for (int j = 0; j < ySize; j++) {
            for (int i = 0; i < xSize; i++) {
                newForm[ySize * i + (ySize - j - 1)] = form[xSize * j + i];
            }
        }
        System.arraycopy(newForm, 0, form, 0, xSize * ySize);
        int temp = ySize;
        ySize = xSize;
        xSize = temp;
        updateBlocks();
    }

    private void updateBlocks() {
        int counter = 0;

        for (int j = 0; j < ySize; j++) {
            for (int i = 0; i < xSize; i++) {
                if (form[xSize * j + i] == 1) {
                    Block currentBlock = blocks.get(counter);
                    currentBlock.setxCoord(i);
                    currentBlock.setyCoord(j);
                    counter++;
                }
            }
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public boolean blockExist(int i, int j) {
        for (Block block : blocks) {
            if (block.getxCoord() == i && block.getyCoord() == j) {
                return true;
            }
        }
        return false;
    }

    public void setColorIndex(int colorIndex) {
        for (Block block : blocks) {
            block.setColorIndex(colorIndex);
        }
    }
}
